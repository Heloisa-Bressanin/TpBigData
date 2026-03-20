<script setup lang="ts">
import type { LivreurStats } from '~/composables/useLivraisons'

const { getStatsByLivreur, getStats } = useLivraisons()

const { data: statsLivreurs } = await useAsyncData('stats-livreurs', () => getStatsByLivreur())
const { data: statsGlobales } = await useAsyncData('stats-globales', () => getStats())

const columns = [
  { key: 'livreur',   label: 'Livreur' },
  { key: 'total',     label: 'Total' },
  { key: 'livres',    label: 'Livrés' },
  { key: 'enTransit', label: 'En transit' },
  { key: 'enAttente', label: 'En attente' },
  { key: 'echecs',    label: 'Échecs' },
  { key: 'tauxReussite', label: 'Taux réussite' }
]

const rows = computed(() =>
  (statsLivreurs.value ?? []).map((s: LivreurStats) => ({
    ...s,
    tauxReussite: s.total > 0 ? Math.round((s.livres / s.total) * 100) : 0
  }))
)

const totalGlobal = computed(() =>
  (statsGlobales.value?.en_attente ?? 0) +
  (statsGlobales.value?.en_transit ?? 0) +
  (statsGlobales.value?.livre ?? 0) +
  (statsGlobales.value?.echec ?? 0)
)
</script>

<template>
  <div class="space-y-8">
    <div>
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Statistiques</h1>
      <p class="text-gray-500 mt-1">Performances par livreur — agrégation MongoDB</p>
    </div>

    <!-- Résumé global -->
    <div class="grid grid-cols-2 lg:grid-cols-5 gap-4">
      <UCard class="text-center col-span-2 lg:col-span-1">
        <div class="py-2">
          <p class="text-3xl font-bold text-gray-900 dark:text-white">{{ totalGlobal }}</p>
          <p class="text-sm text-gray-500 mt-1">Total livraisons</p>
        </div>
      </UCard>
      <UCard class="text-center">
        <div class="py-2">
          <p class="text-3xl font-bold text-green-500">{{ statsGlobales?.livre ?? 0 }}</p>
          <p class="text-sm text-gray-500 mt-1">Livrés</p>
        </div>
      </UCard>
      <UCard class="text-center">
        <div class="py-2">
          <p class="text-3xl font-bold text-blue-500">{{ statsGlobales?.en_transit ?? 0 }}</p>
          <p class="text-sm text-gray-500 mt-1">En transit</p>
        </div>
      </UCard>
      <UCard class="text-center">
        <div class="py-2">
          <p class="text-3xl font-bold text-yellow-500">{{ statsGlobales?.en_attente ?? 0 }}</p>
          <p class="text-sm text-gray-500 mt-1">En attente</p>
        </div>
      </UCard>
      <UCard class="text-center">
        <div class="py-2">
          <p class="text-3xl font-bold text-red-500">{{ statsGlobales?.echec ?? 0 }}</p>
          <p class="text-sm text-gray-500 mt-1">Échecs</p>
        </div>
      </UCard>
    </div>

    <!-- Tableau par livreur -->
    <UCard>
      <template #header>
        <h2 class="font-semibold text-gray-900 dark:text-white">Performance par livreur</h2>
      </template>

      <UTable :rows="rows" :columns="columns">
        <template #livres-data="{ row }">
          <span class="text-green-600 font-semibold">{{ row.livres }}</span>
        </template>

        <template #echecs-data="{ row }">
          <span class="text-red-500 font-semibold">{{ row.echecs }}</span>
        </template>

        <template #tauxReussite-data="{ row }">
          <div class="flex items-center gap-2">
            <UProgress
              :value="row.tauxReussite"
              :color="row.tauxReussite >= 80 ? 'green' : row.tauxReussite >= 50 ? 'yellow' : 'red'"
              size="sm"
              class="w-20"
            />
            <span class="text-sm font-semibold">{{ row.tauxReussite }}%</span>
          </div>
        </template>
      </UTable>
    </UCard>
  </div>
</template>
