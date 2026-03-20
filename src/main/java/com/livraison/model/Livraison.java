package com.livraison.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "livraisons")
public class Livraison {

    @Id
    private String id;
    private String colisId;
    private String livreur;
    private String statut;
    private LocalDateTime timestamp;
    private Localisation localisation;
    private String clientNom;
    private String adresseDest;
    private String priorite;

    public Livraison() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getColisId() { return colisId; }
    public void setColisId(String colisId) { this.colisId = colisId; }

    public String getLivreur() { return livreur; }
    public void setLivreur(String livreur) { this.livreur = livreur; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Localisation getLocalisation() { return localisation; }
    public void setLocalisation(Localisation localisation) { this.localisation = localisation; }

    public String getClientNom() { return clientNom; }
    public void setClientNom(String clientNom) { this.clientNom = clientNom; }

    public String getAdresseDest() { return adresseDest; }
    public void setAdresseDest(String adresseDest) { this.adresseDest = adresseDest; }

    public String getPriorite() { return priorite; }
    public void setPriorite(String priorite) { this.priorite = priorite; }
}
