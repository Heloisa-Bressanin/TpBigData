# Projet Big Data - Suivi de Livraisons Urbaines

Application fullstack de suivi de livraisons en temps réel, construite avec Spring Boot, MongoDB, Redis et Nuxt.js.

---

## Architecture

```
tp-bigData/
├── src/                        ← Backend Spring Boot (Java)
├── livraisons-frontend/        ← Frontend Nuxt.js
├── docker-compose.yml          ← MongoDB + Redis
├── API.md                      ← Documentation des endpoints
└── pom.xml                     ← Dépendances Maven
```

---

## Stack technique

| Couche      | Technologie              | Rôle                              |
|-------------|--------------------------|-----------------------------------|
| Backend     | Spring Boot 3.4 (Java)   | API REST                          |
| Base de données | MongoDB 7.0          | Stockage des livraisons           |
| Cache       | Redis 7.2                | Cache des statuts de colis        |
| Frontend    | Nuxt 3 + Nuxt UI         | Interface utilisateur             |
| Conteneurs  | Docker Compose           | MongoDB + Redis en local          |

---

## Ce qui a été mis en place

### Étape 1 — Modélisation des données (MongoDB)

Chaque livraison est un document avec les champs suivants :

```json
{
  "colisId":     "COLIS-001",
  "livreur":     "Marc Dupont",
  "statut":      "en_attente | en_transit | livre | echec",
  "timestamp":   "2026-03-20T10:30:00",
  "priorite":    "normal | express",
  "clientNom":   "Sophie Martin",
  "adresseDest": "12 Rue de la République, Lyon",
  "localisation": { "lat": 45.764, "lng": 4.835, "ville": "Lyon", "zone": "2eme" }
}
```

### Étape 2 — Pattern DAO (Java)

Séparation claire des responsabilités via des interfaces et leurs implémentations :

```
dao/
├── LivraisonDAO.java       ← interface (findAll, findByStatut, save, delete...)
├── CacheDAO.java           ← interface (getStatut, setStatut, deleteStatut...)
└── impl/
    ├── LivraisonDAOImpl    ← implémentation MongoDB via MongoRepository
    └── CacheDAOImpl        ← implémentation Redis avec TTL 30 min
```

### Étape 3 — Logique métier (Service)

Le `LivraisonService` implémente une stratégie **cache-aside** :

1. Lors d'une lecture de statut → cherche d'abord dans **Redis**
2. Si absent → fallback sur **MongoDB** + mise en cache automatique
3. Lors d'une mise à jour → écrit dans **MongoDB** ET met à jour **Redis**
4. Lors d'une suppression → supprime de **MongoDB** ET invalide **Redis**

### Étape 4 — API REST (Controller)

9 endpoints disponibles sur `http://localhost:8080/api/livraisons` :

| Méthode | Endpoint                          | Description                  |
|---------|-----------------------------------|------------------------------|
| GET     | `/`                               | Toutes les livraisons        |
| GET     | `/{id}`                           | Par ID MongoDB               |
| GET     | `/colis/{colisId}/statut`         | Statut (Redis → MongoDB)     |
| GET     | `/statut/{statut}`                | Filtrer par statut           |
| GET     | `/livreur/{livreur}`              | Filtrer par livreur          |
| GET     | `/stats`                          | Comptage par statut          |
| POST    | `/`                               | Créer une livraison          |
| PATCH   | `/colis/{colisId}/statut`         | Mettre à jour le statut      |
| DELETE  | `/{id}`                           | Supprimer une livraison      |

### Étape 5 — Configuration CORS

Autorisation des appels cross-origin depuis le frontend (`localhost:3000`) vers le backend (`localhost:8080`).

### Étape 6 — Frontend Nuxt.js

3 pages construites avec **Nuxt UI** :

- **`/`** — Dashboard avec compteurs de stats (en attente, en transit, livré, échec) et les 5 dernières livraisons
- **`/livraisons`** — Liste complète avec recherche, filtre par statut, modification de statut (modal), suppression
- **`/nouvelle`** — Formulaire de création d'une livraison

Toute la communication avec l'API est centralisée dans le composable `useLivraisons.ts`.

---

## Lancer le projet

### Prérequis
- Java 17+
- Maven
- Docker
- Node.js 20.19+

### 1. Démarrer MongoDB et Redis

```bash
docker-compose up -d
```

### 2. Importer les données de test

```bash
mongoimport --db livraisons_db --collection livraisons \
  --file src/main/resources/data/livraisons.json --jsonArray
```

### 3. Lancer le backend

```bash
mvn spring-boot:run
```

API disponible sur `http://localhost:8080`

### 4. Lancer le frontend

```bash
cd livraisons-frontend
npm install
npm run dev
```

Interface disponible sur `http://localhost:3000`
