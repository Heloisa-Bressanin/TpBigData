# API - Suivi de Livraisons

Base URL : `http://localhost:8080/api/livraisons`

---

## Modèle Livraison

```json
{
  "id":          "string  — ID MongoDB (auto-généré)",
  "colisId":     "string  — identifiant du colis (ex: COLIS-001)",
  "livreur":     "string  — nom du livreur",
  "statut":      "string  — en_attente | en_transit | livre | echec",
  "timestamp":   "string  — date ISO 8601 (ex: 2026-03-20T10:30:00)",
  "priorite":    "string  — normal | express",
  "clientNom":   "string  — nom du client",
  "adresseDest": "string  — adresse de destination",
  "localisation": {
    "lat":  "number — latitude",
    "lng":  "number — longitude",
    "ville": "string",
    "zone":  "string"
  }
}
```

---

## Endpoints

### 1. Toutes les livraisons

```
GET /api/livraisons
```

**Réponse 200**
```json
[
  {
    "id": "64abc123",
    "colisId": "COLIS-001",
    "livreur": "Marc Dupont",
    "statut": "livre",
    "timestamp": "2026-03-18T10:30:00",
    "localisation": { "lat": 45.764, "lng": 4.835, "ville": "Lyon", "zone": "2eme" },
    "clientNom": "Sophie Martin",
    "adresseDest": "12 Rue de la République, Lyon",
    "priorite": "express"
  }
]
```

---

### 2. Livraison par ID

```
GET /api/livraisons/{id}
```

| Paramètre | Type   | Description         |
|-----------|--------|---------------------|
| `id`      | string | ID MongoDB du colis |

**Réponse 200** — objet `Livraison`

**Réponse 404** — si l'ID n'existe pas

---

### 3. Statut d'un colis (Redis → MongoDB)

```
GET /api/livraisons/colis/{colisId}/statut
```

| Paramètre | Type   | Description            |
|-----------|--------|------------------------|
| `colisId` | string | Identifiant du colis   |

**Réponse 200**
```json
{
  "colisId": "COLIS-001",
  "statut": "livre (cache)"
}
```
> Le suffixe `(cache)` = valeur venant de Redis. `(db)` = fallback MongoDB.

---

### 4. Filtrer par statut

```
GET /api/livraisons/statut/{statut}
```

| Paramètre | Type   | Valeurs possibles                          |
|-----------|--------|--------------------------------------------|
| `statut`  | string | `en_attente` `en_transit` `livre` `echec`  |

**Réponse 200** — tableau de `Livraison`

---

### 5. Filtrer par livreur

```
GET /api/livraisons/livreur/{livreur}
```

| Paramètre | Type   | Description       |
|-----------|--------|-------------------|
| `livreur` | string | Nom du livreur    |

**Réponse 200** — tableau de `Livraison`

---

### 6. Statistiques par statut

```
GET /api/livraisons/stats
```

**Réponse 200**
```json
{
  "en_attente": 3,
  "en_transit": 5,
  "livre": 12,
  "echec": 1
}
```

---

### 7. Créer une livraison

```
POST /api/livraisons
Content-Type: application/json
```

**Body**
```json
{
  "colisId": "COLIS-007",
  "livreur": "Julie Bernard",
  "statut": "en_attente",
  "localisation": {
    "lat": 45.75,
    "lng": 4.83,
    "ville": "Lyon",
    "zone": "1er"
  },
  "clientNom": "Paul Duval",
  "adresseDest": "10 Rue Victor Hugo, Lyon",
  "priorite": "express"
}
```

**Réponse 200** — objet `Livraison` créé avec `id` et `timestamp` générés

---

### 8. Mettre à jour le statut

```
PATCH /api/livraisons/colis/{colisId}/statut
Content-Type: application/json
```

| Paramètre | Type   | Description          |
|-----------|--------|----------------------|
| `colisId` | string | Identifiant du colis |

**Body**
```json
{
  "statut": "livre"
}
```

**Réponse 200** — objet `Livraison` mis à jour

**Réponse 404** — si le colis n'existe pas

> Met à jour MongoDB **et** invalide le cache Redis automatiquement.

---

### 9. Supprimer une livraison

```
DELETE /api/livraisons/{id}
```

| Paramètre | Type   | Description         |
|-----------|--------|---------------------|
| `id`      | string | ID MongoDB du colis |

**Réponse 204** — suppression réussie (pas de body)

> Supprime aussi l'entrée Redis associée.
