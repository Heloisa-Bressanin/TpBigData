package com.livraison.dao.impl;

import com.livraison.dao.LivraisonDAO;
import com.livraison.model.Livraison;
import com.livraison.repository.LivraisonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LivraisonDAOImpl implements LivraisonDAO {

    @Autowired
    private LivraisonRepository repository;

    @Override
    public List<Livraison> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Livraison> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Livraison> findByColisId(String colisId) {
        return repository.findByColisId(colisId);
    }

    @Override
    public List<Livraison> findByStatut(String statut) {
        return repository.findByStatut(statut);
    }

    @Override
    public List<Livraison> findByLivreur(String livreur) {
        return repository.findByLivreur(livreur);
    }

    @Override
    public Livraison save(Livraison livraison) {
        return repository.save(livraison);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public long countByStatut(String statut) {
        return repository.countByStatut(statut);
    }
}
