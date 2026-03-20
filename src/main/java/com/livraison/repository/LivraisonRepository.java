package com.livraison.repository;

import com.livraison.model.Livraison;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivraisonRepository extends MongoRepository<Livraison, String> {
    Optional<Livraison> findByColisId(String colisId);
    List<Livraison> findByStatut(String statut);
    List<Livraison> findByLivreur(String livreur);
    long countByStatut(String statut);
}
