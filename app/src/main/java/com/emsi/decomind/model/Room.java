/*
 * Projet : DecoMind
 * Fichier : Room.java
 * Description : Modèle : pièce d'un projet (dimensions, photo, style, meubles)
 * Responsable(s) :
 * - Lahroum Amina
 */

package com.emsi.decomind.model;

import java.util.ArrayList;

public class Room {
    private final int id;
    private String type;
    private String name;
    private double length;
    private double width;
    private double height;
    private String photoUri = "";
    private String style = "";
    private final ArrayList<ColorOption> colors = new ArrayList<>();
    private final ArrayList<SelectedItem> items = new ArrayList<>();

    public Room(int id, String type, String name, double length, double width, double height) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.length = length;
        this.width = width;
        this.height = height;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getLength() { return length; }
    public void setLength(double length) { this.length = length; }
    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
    public ArrayList<ColorOption> getColors() { return colors; }
    public ArrayList<SelectedItem> getItems() { return items; }

    public double getArea() {
        return length * width;
    }

    public double getCost() {
        double total = 0;
        for (SelectedItem item : items) {
            total += item.getTotal();
        }
        return total;
    }

    public int getItemsCount() {
        int count = 0;
        for (SelectedItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    @Override
    public String toString() {
        return name;
    }
}