/*
 * Projet : DecoMind
 * Fichier : Palette.java
 * Description : Modèle : palette de couleurs de décoration
 * Responsable(s) :
 * - Gamran Nour El Houda
 */

package com.emsi.decomind.model;

import java.util.ArrayList;

public class Palette {
    private final String name;
    private final ArrayList<ColorOption> colors = new ArrayList<>();

    public Palette(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public ArrayList<ColorOption> getColors() { return colors; }

    @Override
    public String toString() {
        return name;
    }
}