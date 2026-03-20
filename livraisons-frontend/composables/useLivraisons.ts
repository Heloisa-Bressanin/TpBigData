export interface Localisation {
  lat: number
  lng: number
  ville: string
  zone: string
}

export interface Livraison {
  id: string
  colisId: string
  livreur: string
  statut: 'en_attente' | 'en_transit' | 'livre' | 'echec'
  timestamp: string
  localisation: Localisation
  clientNom: string
  adresseDest: string
  priorite: 'normal' | 'express'
}

export interface Stats {
  en_attente: number
  en_transit: number
  livre: number
  echec: number
}

export interface LivreurStats {
  livreur: string
  total: number
  livres: number
  enTransit: number
  enAttente: number
  echecs: number
}

export const statutConfig = {
  en_attente: { label: 'En attente',  color: 'yellow', icon: 'i-heroicons-clock' },
  en_transit:  { label: 'En transit', color: 'blue',   icon: 'i-heroicons-truck' },
  livre:       { label: 'Livré',      color: 'green',  icon: 'i-heroicons-check-circle' },
  echec:       { label: 'Échec',      color: 'red',    icon: 'i-heroicons-x-circle' }
} as const

export function useLivraisons() {
  const config = useRuntimeConfig()
  const base = config.public.apiBase

  const getAll = () =>
    $fetch<Livraison[]>(base)

  const getById = (id: string) =>
    $fetch<Livraison>(`${base}/${id}`)

  const getByStatut = (statut: string) =>
    $fetch<Livraison[]>(`${base}/statut/${statut}`)

  const getByLivreur = (livreur: string) =>
    $fetch<Livraison[]>(`${base}/livreur/${livreur}`)

  const getStats = () =>
    $fetch<Stats>(`${base}/stats`)

  const getStatutColis = (colisId: string) =>
    $fetch<{ colisId: string; statut: string }>(`${base}/colis/${colisId}/statut`)

  const create = (data: Partial<Livraison>) =>
    $fetch<Livraison>(base, { method: 'POST', body: data })

  const updateStatut = (colisId: string, statut: string) =>
    $fetch<Livraison>(`${base}/colis/${colisId}/statut`, {
      method: 'PATCH',
      body: { statut }
    })

  const remove = (id: string) =>
    $fetch(`${base}/${id}`, { method: 'DELETE' })

  const getStatsByLivreur = () =>
    $fetch<LivreurStats[]>(`${base}/stats/par-livreur`)

  return { getAll, getById, getByStatut, getByLivreur, getStats, getStatutColis, create, updateStatut, remove, getStatsByLivreur }
}
