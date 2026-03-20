package com.livraison.dao;

import java.util.Optional;

public interface CacheDAO {
    void setStatut(String colisId, String statut);
    Optional<String> getStatut(String colisId);
    void deleteStatut(String colisId);
    boolean exists(String colisId);
}
