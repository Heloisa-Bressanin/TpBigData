package com.livraison.repository;

import com.livraison.model.Livraison;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivraisonRepository extends MongoRepository<Livraison, String> {

//* ?0 = premier paramètre de la méthode
//* ?1 = deuxième paramètre, etc.
//* count = true sur le countByStatut → retourne un nombre au lieu d'une liste



    @Query("{ 'colisId': ?0 }")
    Optional<Livraison> findByColisId(String colisId);

    @Query("{ 'statut': ?0 }")
    List<Livraison> findByStatut(String statut);

    @Query("{ 'livreur': ?0 }")
    List<Livraison> findByLivreur(String livreur);

    @Query(value = "{ 'statut': ?0 }", count = true)
    long countByStatut(String statut);
}
