package com.livraison.service;

import com.livraison.dao.CacheDAO;
import com.livraison.dao.LivraisonDAO;
import com.livraison.model.Livraison;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LivraisonService {

    @Autowired
    private LivraisonDAO livraisonDAO;

    @Autowired
    private CacheDAO cacheDAO;

    public List<Livraison> getAll() {
        return livraisonDAO.findAll();
    }

    public Optional<Livraison> getById(String id) {
        return livraisonDAO.findById(id);
    }

    /**
     * Cherche le statut dans Redis d'abord, fallback sur MongoDB.
     */
    public String getStatutByColis(String colisId) {
        Optional<String> cached = cacheDAO.getStatut(colisId);
        if (cached.isPresent()) {
            return cached.get() + " (cache)";
        }
        return livraisonDAO.findByColisId(colisId)
                .map(l -> {
                    cacheDAO.setStatut(colisId, l.getStatut());
                    return l.getStatut() + " (db)";
                })
                .orElse("Colis introuvable");
    }

    public List<Livraison> getByStatut(String statut) {
        return livraisonDAO.findByStatut(statut);
    }

    public List<Livraison> getByLivreur(String livreur) {
        return livraisonDAO.findByLivreur(livreur);
    }

    public Livraison create(Livraison livraison) {
        livraison.setTimestamp(LocalDateTime.now());
        Livraison saved = livraisonDAO.save(livraison);
        cacheDAO.setStatut(saved.getColisId(), saved.getStatut());
        return saved;
    }

    /**
     * Met à jour le statut dans MongoDB ET invalide le cache Redis.
     */
    public Optional<Livraison> updateStatut(String colisId, String nouveauStatut) {
        return livraisonDAO.findByColisId(colisId).map(l -> {
            l.setStatut(nouveauStatut);
            l.setTimestamp(LocalDateTime.now());
            Livraison updated = livraisonDAO.save(l);
            cacheDAO.setStatut(colisId, nouveauStatut);
            return updated;
        });
    }

    public void delete(String id) {
        livraisonDAO.findById(id).ifPresent(l -> {
            cacheDAO.deleteStatut(l.getColisId());
            livraisonDAO.deleteById(id);
        });
    }

    public long countByStatut(String statut) {
        return livraisonDAO.countByStatut(statut);
    }
}
