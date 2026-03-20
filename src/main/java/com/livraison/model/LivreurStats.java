package com.livraison.model;

public class LivreurStats {
    private String livreur;
    private long total;
    private long livres;
    private long enTransit;
    private long enAttente;
    private long echecs;

    public LivreurStats() {}

    public String getLivreur() { return livreur; }
    public void setLivreur(String livreur) { this.livreur = livreur; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public long getLivres() { return livres; }
    public void setLivres(long livres) { this.livres = livres; }

    public long getEnTransit() { return enTransit; }
    public void setEnTransit(long enTransit) { this.enTransit = enTransit; }

    public long getEnAttente() { return enAttente; }
    public void setEnAttente(long enAttente) { this.enAttente = enAttente; }

    public long getEchecs() { return echecs; }
    public void setEchecs(long echecs) { this.echecs = echecs; }
}
