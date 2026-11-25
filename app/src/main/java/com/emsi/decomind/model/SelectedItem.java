/*
 * Projet : DecoMind
 * Fichier : SelectedItem.java
 * Description : Modèle : meuble sélectionné pour une pièce, avec sa quantité
 * Responsable(s) :
 * - Gamran Nour El Houda
 */

package com.emsi.decomind.model;

public class SelectedItem {
    private final Furniture furniture;
    private int quantity;

    public SelectedItem(Furniture furniture, int quantity) {
        this.furniture = furniture;
        this.quantity = quantity;
    }

    public Furniture getFurniture() { return furniture; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotal() {
        return furniture.getPrice() * quantity;
    }
}