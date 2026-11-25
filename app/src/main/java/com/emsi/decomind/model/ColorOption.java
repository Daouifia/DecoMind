/*
 * Projet : DecoMind
 * Fichier : ColorOption.java
 * Description : Modèle : une couleur nommée d'une palette
 * Responsable(s) :
 * - Gamran Nour El Houda
 */

package com.emsi.decomind.model;

public class ColorOption {
    private final String name;
    private final int color;

    public ColorOption(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() { return name; }
    public int getColor() { return color; }

    @Override
    public String toString() {
        return name;
    }
}