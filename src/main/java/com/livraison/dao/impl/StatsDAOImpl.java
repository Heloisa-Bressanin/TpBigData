package com.livraison.dao.impl;

import com.livraison.dao.StatsDAO;
import com.livraison.model.LivreurStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StatsDAOImpl implements StatsDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<LivreurStats> statsParLivreur() {
        Aggregation agg = Aggregation.newAggregation(
            Aggregation.group("livreur")
                .count().as("total")
                .sum(ConditionalOperators
                    .when(Criteria.where("statut").is("livre"))
                    .then(1).otherwise(0)).as("livres")
                .sum(ConditionalOperators
                    .when(Criteria.where("statut").is("en_transit"))
                    .then(1).otherwise(0)).as("enTransit")
                .sum(ConditionalOperators
                    .when(Criteria.where("statut").is("en_attente"))
                    .then(1).otherwise(0)).as("enAttente")
                .sum(ConditionalOperators
                    .when(Criteria.where("statut").is("echec"))
                    .then(1).otherwise(0)).as("echecs"),
            Aggregation.project("total", "livres", "enTransit", "enAttente", "echecs")
                .and("_id").as("livreur"),
            Aggregation.sort(Sort.Direction.DESC, "total")
        );

        return mongoTemplate
            .aggregate(agg, "livraisons", LivreurStats.class)
            .getMappedResults();
    }
}
