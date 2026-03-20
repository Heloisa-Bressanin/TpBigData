package com.livraison.dao;

import com.livraison.model.LivreurStats;

import java.util.List;

public interface StatsDAO {
    List<LivreurStats> statsParLivreur();
}
