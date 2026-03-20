<script setup lang="ts">
import { statutConfig } from '~/composables/useLivraisons'

const route = useRoute()
const { getById } = useLivraisons()

const { data: livraison } = await useAsyncData(
  `livraison-${route.params.id}`,
  () => getById(route.params.id as string)
)

function formatDate(ts: string) {
  return new Date(ts).toLocaleString('fr-FR', { dateStyle: 'long', timeStyle: 'short' })
}
</script>

<template>
  <div class="space-y-6 max-w-2xl">
    <div class="flex items-center gap-3">
      <UButton variant="ghost" icon="i-heroicons-arrow-left" to="/livraisons" />
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
        Détail — {{ livraison?.colisId }}
      </h1>
    </div>

    <div v-if="livraison" class="space-y-4">
      <!-- Statut + Priorité -->
      <UCard>
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <UIcon :name="statutConfig[livraison.statut]?.icon" class="w-6 h-6"
              :class="`text-${statutConfig[livraison.statut]?.color}-500`" />
            <UBadge :color="statutConfig[livraison.statut]?.color" size="lg">
              {{ statutConfig[livraison.statut]?.label }}
            </UBadge>
          </div>
          <UBadge :color="livraison.priorite === 'express' ? 'orange' : 'gray'" variant="outline">
            {{ livraison.priorite }}
          </UBadge>
        </div>
      </UCard>

      <!-- Infos colis -->
      <UCard>
        <template #header>
          <span class="font-semibold">Informations colis</span>
        </template>
        <dl class="space-y-3">
          <div class="flex justify-between">
            <dt class="text-gray-500">ID Colis</dt>
            <dd class="font-mono font-semibold">{{ livraison.colisId }}</dd>
          </div>
          <div class="flex justify-between">
            <dt class="text-gray-500">Livreur</dt>
            <dd>{{ livraison.livreur }}</dd>
          </div>
          <div class="flex justify-between">
            <dt class="text-gray-500">Date</dt>
            <dd>{{ formatDate(livraison.timestamp) }}</dd>
          </div>
        </dl>
      </UCard>

      <!-- Infos client -->
      <UCard>
        <template #header>
          <span class="font-semibold">Client</span>
        </template>
        <dl class="space-y-3">
          <div class="flex justify-between">
            <dt class="text-gray-500">Nom</dt>
            <dd>{{ livraison.clientNom }}</dd>
          </div>
          <div class="flex justify-between">
            <dt class="text-gray-500">Adresse</dt>
            <dd class="text-right">{{ livraison.adresseDest }}</dd>
          </div>
        </dl>
      </UCard>

      <!-- Localisation -->
      <UCard>
        <template #header>
          <span class="font-semibold">Localisation</span>
        </template>
        <dl class="space-y-3">
          <div class="flex justify-between">
            <dt class="text-gray-500">Ville</dt>
            <dd>{{ livraison.localisation?.ville }} — {{ livraison.localisation?.zone }}</dd>
          </div>
          <div class="flex justify-between">
            <dt class="text-gray-500">Coordonnées</dt>
            <dd class="font-mono text-sm">{{ livraison.localisation?.lat }}, {{ livraison.localisation?.lng }}</dd>
          </div>
        </dl>
      </UCard>
    </div>

    <UAlert v-else icon="i-heroicons-exclamation-triangle" color="red" title="Livraison introuvable" />
  </div>
</template>
