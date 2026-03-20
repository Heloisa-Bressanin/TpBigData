package com.livraison.controller;

import com.livraison.model.Livraison;
import com.livraison.model.LivreurStats;
import com.livraison.service.LivraisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/livraisons")
public class LivraisonController {

    @Autowired
    private LivraisonService service;

    // GET /api/livraisons
    @GetMapping
    public List<Livraison> getAll() {
        return service.getAll();
    }

    // GET /api/livraisons/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Livraison> getById(@PathVariable String id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/livraisons/colis/{colisId}/statut
    @GetMapping("/colis/{colisId}/statut")
    public ResponseEntity<Map<String, String>> getStatut(@PathVariable String colisId) {
        String statut = service.getStatutByColis(colisId);
        return ResponseEntity.ok(Map.of("colisId", colisId, "statut", statut));
    }

    // GET /api/livraisons/statut/{statut}
    @GetMapping("/statut/{statut}")
    public List<Livraison> getByStatut(@PathVariable String statut) {
        return service.getByStatut(statut);
    }

    // GET /api/livraisons/livreur/{livreur}
    @GetMapping("/livreur/{livreur}")
    public List<Livraison> getByLivreur(@PathVariable String livreur) {
        return service.getByLivreur(livreur);
    }

    // GET /api/livraisons/stats
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(Map.of(
                "en_attente",  service.countByStatut("en_attente"),
                "en_transit",  service.countByStatut("en_transit"),
                "livre",       service.countByStatut("livre"),
                "echec",       service.countByStatut("echec")
        ));
    }

    // POST /api/livraisons
    @PostMapping
    public ResponseEntity<Livraison> create(@RequestBody Livraison livraison) {
        return ResponseEntity.ok(service.create(livraison));
    }

    // PATCH /api/livraisons/colis/{colisId}/statut
    @PatchMapping("/colis/{colisId}/statut")
    public ResponseEntity<Livraison> updateStatut(
            @PathVariable String colisId,
            @RequestBody Map<String, String> body) {
        String nouveauStatut = body.get("statut");
        return service.updateStatut(colisId, nouveauStatut)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/livraisons/stats/par-livreur
    @GetMapping("/stats/par-livreur")
    public List<LivreurStats> statsParLivreur() {
        return service.statsParLivreur();
    }

    // DELETE /api/livraisons/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
