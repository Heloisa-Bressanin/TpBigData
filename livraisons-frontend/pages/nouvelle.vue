<script setup lang="ts">
const { create } = useLivraisons()
const router = useRouter()
const toast = useToast()

const form = reactive({
  colisId: '',
  livreur: '',
  statut: 'en_attente',
  priorite: 'normal',
  clientNom: '',
  adresseDest: '',
  localisation: {
    lat: 45.764,
    lng: 4.835,
    ville: 'Lyon',
    zone: ''
  }
})

const loading = ref(false)

const statutOptions = [
  { label: 'En attente', value: 'en_attente' },
  { label: 'En transit', value: 'en_transit' },
  { label: 'Livré',      value: 'livre' },
  { label: 'Échec',      value: 'echec' }
]

const prioriteOptions = [
  { label: 'Normal',  value: 'normal' },
  { label: 'Express', value: 'express' }
]

async function soumettre() {
  loading.value = true
  try {
    await create(form)
    toast.add({ title: 'Livraison créée', color: 'green', icon: 'i-heroicons-check-circle' })
    router.push('/livraisons')
  } catch (e) {
    toast.add({ title: 'Erreur lors de la création', color: 'red', icon: 'i-heroicons-x-circle' })
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="max-w-2xl space-y-6">
    <div>
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Nouvelle livraison</h1>
      <p class="text-gray-500 mt-1">Remplissez les informations du colis</p>
    </div>

    <UCard>
      <form class="space-y-5" @submit.prevent="soumettre">
        <div class="grid grid-cols-2 gap-4">
          <UFormGroup label="ID Colis" required>
            <UInput v-model="form.colisId" placeholder="COLIS-007" />
          </UFormGroup>
          <UFormGroup label="Livreur" required>
            <UInput v-model="form.livreur" placeholder="Marc Dupont" />
          </UFormGroup>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <UFormGroup label="Statut">
            <USelect v-model="form.statut" :options="statutOptions" />
          </UFormGroup>
          <UFormGroup label="Priorité">
            <USelect v-model="form.priorite" :options="prioriteOptions" />
          </UFormGroup>
        </div>

        <UFormGroup label="Nom du client" required>
          <UInput v-model="form.clientNom" placeholder="Sophie Martin" />
        </UFormGroup>

        <UFormGroup label="Adresse de destination" required>
          <UInput v-model="form.adresseDest" placeholder="12 Rue de la République, Lyon" />
        </UFormGroup>

        <div class="grid grid-cols-2 gap-4">
          <UFormGroup label="Ville">
            <UInput v-model="form.localisation.ville" placeholder="Lyon" />
          </UFormGroup>
          <UFormGroup label="Zone">
            <UInput v-model="form.localisation.zone" placeholder="2eme" />
          </UFormGroup>
        </div>

        <div class="flex justify-end gap-2 pt-2">
          <UButton variant="ghost" to="/livraisons">Annuler</UButton>
          <UButton type="submit" color="primary" :loading="loading" icon="i-heroicons-paper-airplane">
            Créer la livraison
          </UButton>
        </div>
      </form>
    </UCard>
  </div>
</template>
