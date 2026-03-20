package com.livraison.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "livraisons")
public class Livraison {

    @Id
    private String id;

    private String colisId;
    private String livreur;
    private String statut; // en_attente, en_transit, livre, echec
    private LocalDateTime timestamp;
    private Localisation localisation;
    private String clientNom;
    private String adresseDest;
    private String priorite; // normal, express
}
