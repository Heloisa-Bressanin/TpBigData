# Architecture & Justifications — Suivi de Livraisons Urbaines

---

## Conformité avec l'énoncé

| Exigence | Statut | Détail |
|---|---|---|
| Au moins 2 BDD dont MongoDB | ✅ | MongoDB (stockage) + Redis (cache) |
| Pattern DAO | ✅ | `LivraisonDAO`, `CacheDAO`, `StatsDAO` + leurs implémentations |
| Jeu de données fictif | ✅ | 50 documents JSON générés |
| Document JSON type | ✅ | Modèle `Livraison` avec timestamp, source, statut, localisation |
| Métrique remonte jusqu'à l'écran | ✅ | Dashboard + page `/stats` avec taux de réussite par livreur |
| Démo API en direct | ✅ | 9 endpoints REST sur `localhost:8080` |

---

## Pourquoi cette architecture globale ?

Le cas d'usage est la **logistique urbaine** : des livreurs mettent à jour le statut de colis en temps réel, et un dashboard doit afficher ces métriques instantanément.

- **MongoDB** est choisi pour sa flexibilité de schéma (un document de livraison peut évoluer : ajouter des champs sans migration) et sa capacité à stocker des données géolocalisées (lat/lng). C'est la source de vérité.
- **Redis** est choisi comme couche de cache pour les statuts, qui sont la donnée la plus consultée (chaque scan de colis déclenche une lecture). Redis répond en < 1ms contre ~10ms pour MongoDB.

---

## 1. Transit de la donnée

**Oui**, la donnée transite d'abord par Redis avant d'être persistée dans MongoDB dans un seul cas : lors d'une **mise à jour de statut**.

Le flux normal est :

```
Création → MongoDB (source de vérité) → Redis (cache mis à jour)
Lecture  → Redis d'abord → fallback MongoDB si absent
Update   → MongoDB mis à jour → Redis invalidé et réécrit
```

---

## 2. Synchronisation en temps réel

La synchronisation est **synchrone et immédiate** mais pas bidirectionnelle. C'est une stratégie **cache-aside** :

- Redis ne "tire" pas les données depuis MongoDB automatiquement
- C'est le service Java (`LivraisonService`) qui orchestre : il écrit dans MongoDB puis met à jour Redis dans la même transaction applicative
- TTL de 30 minutes sur les clés Redis : si le service plante entre les deux écritures, Redis expirera et rechargera depuis MongoDB

Il n'y a pas de synchronisation autonome (pas de Kafka, pas de change stream MongoDB) — c'est un choix de simplicité assumé.

---

## 3. Cycle de vie de la donnée

```
1. CRÉATION
   Un livreur prend en charge un colis → POST /api/livraisons
   → Écrit dans MongoDB (persistance)
   → Statut mis en cache Redis (TTL 30min)

2. LECTURE
   Dashboard consulte le statut → GET /colis/{id}/statut
   → Cherche Redis (rapide)
   → Si absent : lit MongoDB + réalimente Redis

3. MISE À JOUR
   Livreur scanne le colis → PATCH /colis/{id}/statut
   → Met à jour MongoDB (timestamp mis à jour)
   → Écrase Redis avec le nouveau statut

4. EXPIRATION
   Si inactif 30min → clé Redis supprimée automatiquement
   → Prochaine lecture recharge depuis MongoDB

5. SUPPRESSION
   Archivage ou erreur → DELETE /api/livraisons/{id}
   → Supprimé de MongoDB
   → Clé Redis supprimée manuellement
```

---

## Pourquoi AP sur le théorème de CAP ?

Notre système choisit **AP (Availability + Partition tolerance)** au détriment de la Consistency stricte.

**Justification :**
- MongoDB en mode standalone est CP par défaut, mais Redis est AP par nature (si Redis tombe, on continue de lire MongoDB — le service reste disponible)
- On accepte une **inconsistance temporelle** : pendant le TTL de 30 min, Redis peut retourner un statut légèrement en retard si MongoDB a été mis à jour directement
- Dans la logistique urbaine, il vaut mieux **afficher un statut potentiellement vieux** que de bloquer le système. Un livreur ne peut pas attendre qu'une BDD soit synchronisée pour scanner un colis

> Si on avait choisi CP, une panne Redis bloquerait toutes les lectures de statut. Ce n'est pas acceptable en production.

---

## Document JSON type

```json
{
  "_id": "ObjectId(...)",
  "colisId": "COLIS-042",
  "livreur": "Julie Bernard",
  "statut": "livre",
  "timestamp": "2026-03-18T09:30:00",
  "priorite": "express",
  "clientNom": "Rémi Vasseur",
  "adresseDest": "20 Rue Auguste Comte, Lyon",
  "localisation": {
    "lat": 45.763,
    "lng": 4.841,
    "ville": "Lyon",
    "zone": "2eme"
  }
}
```

Ce document sert concrètement à **alimenter le dashboard** :
- `statut` → compteurs en temps réel (en attente / transit / livré / échec)
- `livreur` + `statut` → agrégation MongoDB pour le taux de réussite par livreur
- `localisation` → carte des livraisons par zone
- `timestamp` → courbe de livraisons par jour
