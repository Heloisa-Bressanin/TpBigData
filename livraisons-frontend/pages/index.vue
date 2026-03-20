<script setup lang="ts">
import { statutConfig } from '~/composables/useLivraisons'

const { getStats, getAll } = useLivraisons()

const { data: stats } = await useAsyncData('stats', () => getStats())
const { data: livraisons } = await useAsyncData('livraisons', () => getAll())

const statCards = computed(() => [
  {
    key: 'en_attente',
    label: 'En attente',
    value: stats.value?.en_attente ?? 0,
    icon: 'i-heroicons-clock',
    color: 'yellow'
  },
  {
    key: 'en_transit',
    label: 'En transit',
    value: stats.value?.en_transit ?? 0,
    icon: 'i-heroicons-truck',
    color: 'blue'
  },
  {
    key: 'livre',
    label: 'Livrés',
    value: stats.value?.livre ?? 0,
    icon: 'i-heroicons-check-circle',
    color: 'green'
  },
  {
    key: 'echec',
    label: 'Échecs',
    value: stats.value?.echec ?? 0,
    icon: 'i-heroicons-x-circle',
    color: 'red'
  }
])

const columns = [
  { key: 'colisId',    label: 'Colis' },
  { key: 'livreur',   label: 'Livreur' },
  { key: 'statut',    label: 'Statut' },
  { key: 'priorite',  label: 'Priorité' },
  { key: 'clientNom', label: 'Client' },
  { key: 'timestamp', label: 'Date' },
  { key: 'actions',   label: '' }
]

const dernieres = computed(() =>
  (livraisons.value ?? [])
    .slice()
    .sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime())
    .slice(0, 5)
)

function formatDate(ts: string) {
  return new Date(ts).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' })
}
</script>

<template>
  <div class="space-y-8">
    <div>
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Dashboard</h1>
      <p class="text-gray-500 mt-1">Vue d'ensemble des livraisons en cours</p>
    </div>

    <!-- Stats -->
    <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
      <UCard
        v-for="card in statCards"
        :key="card.key"
        class="text-center"
      >
        <div class="flex flex-col items-center gap-2 py-2">
          <UIcon :name="card.icon" class="w-8 h-8" :class="`text-${card.color}-500`" />
          <span class="text-3xl font-bold text-gray-900 dark:text-white">{{ card.value }}</span>
          <span class="text-sm text-gray-500">{{ card.label }}</span>
        </div>
      </UCard>
    </div>

    <!-- Dernières livraisons -->
    <UCard>
      <template #header>
        <div class="flex items-center justify-between">
          <h2 class="font-semibold text-gray-900 dark:text-white">Dernières livraisons</h2>
          <UButton variant="ghost" to="/livraisons" trailing-icon="i-heroicons-arrow-right" size="sm">
            Voir tout
          </UButton>
        </div>
      </template>

      <UTable :rows="dernieres" :columns="columns">
        <template #statut-data="{ row }">
          <UBadge
            :color="statutConfig[row.statut]?.color"
            variant="subtle"
          >
            {{ statutConfig[row.statut]?.label }}
          </UBadge>
        </template>

        <template #priorite-data="{ row }">
          <UBadge :color="row.priorite === 'express' ? 'orange' : 'gray'" variant="outline" size="xs">
            {{ row.priorite }}
          </UBadge>
        </template>

        <template #timestamp-data="{ row }">
          <span class="text-gray-500 text-sm">{{ formatDate(row.timestamp) }}</span>
        </template>

        <template #actions-data="{ row }">
          <UButton
            variant="ghost"
            icon="i-heroicons-eye"
            size="xs"
            :to="`/livraisons/${row.id}`"
          />
        </template>
      </UTable>
    </UCard>
  </div>
</template>
