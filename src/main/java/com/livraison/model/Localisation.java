package com.livraison.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Localisation {
    private double lat;
    private double lng;
    private String ville;
    private String zone;
}
