<script setup lang="ts">
import { statutConfig, type Livraison } from '~/composables/useLivraisons'

const { getAll, getByStatut, getByLivreur, getStatutColis, updateStatut, remove } = useLivraisons()

const filtreStatut = ref('')
const filtreLivreur = ref('')
const searchColisId = ref('')
const resultStatutColis = ref<{ colisId: string; statut: string } | null>(null)
const loadingStatut = ref(false)

const { data: livraisons, refresh } = await useAsyncData(
  'livraisons-list',
  () => {
    if (filtreStatut.value) return getByStatut(filtreStatut.value)
    if (filtreLivreur.value) return getByLivreur(filtreLivreur.value)
    return getAll()
  }
)

async function appliquerFiltreStatut(val: string) {
  filtreStatut.value = val
  filtreLivreur.value = ''
  await refresh()
}

async function appliquerFiltreLivreur() {
  filtreStatut.value = ''
  await refresh()
}

async function rechercherStatutColis() {
  if (!searchColisId.value) return
  loadingStatut.value = true
  try {
    resultStatutColis.value = await getStatutColis(searchColisId.value)
  } catch {
    resultStatutColis.value = null
  } finally {
    loadingStatut.value = false
  }
}

const statutOptions = [
  { label: 'Tous les statuts', value: '' },
  { label: 'En attente',       value: 'en_attente' },
  { label: 'En transit',       value: 'en_transit' },
  { label: 'Livré',            value: 'livre' },
  { label: 'Échec',            value: 'echec' }
]

const columns = [
  { key: 'colisId',     label: 'Colis' },
  { key: 'livreur',    label: 'Livreur' },
  { key: 'statut',     label: 'Statut' },
  { key: 'priorite',   label: 'Priorité' },
  { key: 'clientNom',  label: 'Client' },
  { key: 'adresseDest',label: 'Adresse' },
  { key: 'timestamp',  label: 'Date' },
  { key: 'actions',    label: '' }
]

// Modal mise à jour statut
const modalOpen = ref(false)
const livraisonSelectionnee = ref<Livraison | null>(null)
const nouveauStatut = ref('')

function ouvrirModal(l: Livraison) {
  livraisonSelectionnee.value = l
  nouveauStatut.value = l.statut
  modalOpen.value = true
}

async function sauvegarderStatut() {
  if (!livraisonSelectionnee.value) return
  await updateStatut(livraisonSelectionnee.value.colisId, nouveauStatut.value)
  modalOpen.value = false
  await refresh()
}

async function supprimerLivraison(id: string) {
  await remove(id)
  await refresh()
}

function formatDate(ts: string) {
  return new Date(ts).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' })
}
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Livraisons</h1>
        <p class="text-gray-500 mt-1">{{ livraisons?.length ?? 0 }} résultat(s)</p>
      </div>
      <UButton to="/nouvelle" icon="i-heroicons-plus" color="primary">Nouvelle livraison</UButton>
    </div>

    <!-- Filtres serveur -->
    <div class="flex gap-3 flex-wrap">
      <USelect
        v-model="filtreStatut"
        :options="statutOptions"
        class="w-48"
        @change="appliquerFiltreStatut(filtreStatut)"
      />
      <div class="flex gap-2 flex-1">
        <UInput
          v-model="filtreLivreur"
          placeholder="Filtrer par livreur..."
          icon="i-heroicons-user"
          class="flex-1"
          @keyup.enter="appliquerFiltreLivreur"
        />
        <UButton icon="i-heroicons-magnifying-glass" @click="appliquerFiltreLivreur">
          Filtrer
        </UButton>
        <UButton variant="ghost" icon="i-heroicons-x-mark" @click="filtreLivreur = ''; filtreStatut = ''; refresh()">
          Reset
        </UButton>
      </div>
    </div>

    <!-- Recherche statut par colis ID (appel Redis → MongoDB) -->
    <UCard>
      <template #header>
        <span class="font-semibold text-sm text-gray-600 dark:text-gray-300">
          Recherche rapide de statut
        </span>
      </template>
      <div class="flex gap-2">
        <UInput
          v-model="searchColisId"
          placeholder="ex: COLIS-001"
          icon="i-heroicons-qr-code"
          class="flex-1"
          @keyup.enter="rechercherStatutColis"
        />
        <UButton :loading="loadingStatut" @click="rechercherStatutColis">Rechercher</UButton>
      </div>
      <div v-if="resultStatutColis" class="mt-3 flex items-center gap-3">
        <span class="font-mono text-sm text-gray-700 dark:text-gray-300">{{ resultStatutColis.colisId }}</span>
        <UBadge :color="statutConfig[resultStatutColis.statut.split(' ')[0] as keyof typeof statutConfig]?.color ?? 'gray'" variant="subtle">
          {{ resultStatutColis.statut }}
        </UBadge>
      </div>
    </UCard>

    <!-- Tableau -->
    <UCard>
      <UTable :rows="livraisons ?? []" :columns="columns">
        <template #statut-data="{ row }">
          <UBadge :color="statutConfig[row.statut]?.color" variant="subtle">
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
          <div class="flex gap-1">
            <UButton variant="ghost" icon="i-heroicons-eye" size="xs" :to="`/livraisons/${row.id}`" />
            <UButton variant="ghost" icon="i-heroicons-pencil-square" size="xs" @click="ouvrirModal(row)" />
            <UButton variant="ghost" icon="i-heroicons-trash" size="xs" color="red" @click="supprimerLivraison(row.id)" />
          </div>
        </template>
      </UTable>
    </UCard>

    <!-- Modal changement de statut -->
    <UModal v-model="modalOpen">
      <UCard>
        <template #header>
          <h3 class="font-semibold">Modifier le statut — {{ livraisonSelectionnee?.colisId }}</h3>
        </template>
        <USelect
          v-model="nouveauStatut"
          :options="statutOptions.filter(o => o.value !== '')"
          option-attribute="label"
          value-attribute="value"
        />
        <template #footer>
          <div class="flex justify-end gap-2">
            <UButton variant="ghost" @click="modalOpen = false">Annuler</UButton>
            <UButton color="primary" @click="sauvegarderStatut">Sauvegarder</UButton>
          </div>
        </template>
      </UCard>
    </UModal>
  </div>
</template>
