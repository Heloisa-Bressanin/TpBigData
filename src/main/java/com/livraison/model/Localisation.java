package com.livraison.model;

public class Localisation {
    private double lat;
    private double lng;
    private String ville;
    private String zone;

    public Localisation() {}

    public Localisation(double lat, double lng, String ville, String zone) {
        this.lat = lat;
        this.lng = lng;
        this.ville = ville;
        this.zone = zone;
    }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }
}
