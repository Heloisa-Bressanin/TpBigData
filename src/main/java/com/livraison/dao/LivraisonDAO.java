package com.livraison.dao;

import com.livraison.model.Livraison;

import java.util.List;
import java.util.Optional;

public interface LivraisonDAO {
    List<Livraison> findAll();
    Optional<Livraison> findById(String id);
    Optional<Livraison> findByColisId(String colisId);
    List<Livraison> findByStatut(String statut);
    List<Livraison> findByLivreur(String livreur);
    Livraison save(Livraison livraison);
    void deleteById(String id);
    long countByStatut(String statut);
}
