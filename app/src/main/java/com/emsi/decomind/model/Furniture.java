/*
 * Projet : DecoMind
 * Fichier : Furniture.java
 * Description : Modèle : meuble ou accessoire du catalogue
 * Responsable(s) :
 * - El Guettafi Soukaina
 */

package com.emsi.decomind.model;

public class Furniture {
    private final int id;
    private final String name;
    private final String category;
    private final double price;
    private final String description;
    private final int imageRes;

    public Furniture(int id, String name, String category, double price,
                     String description, int imageRes) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageRes = imageRes;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public int getImageRes() { return imageRes; }

    @Override
    public String toString() {
        return name;
    }
}