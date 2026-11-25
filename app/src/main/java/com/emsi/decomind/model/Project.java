/*
 * Projet : DecoMind
 * Fichier : Project.java
 * Description : Modèle : projet de décoration (pièces, budget, rappel)
 * Responsable(s) :
 * - Aouifia Doha
 */

package com.emsi.decomind.model;

import java.util.ArrayList;

public class Project {
    private final int id;
    private String name;
    private String description;
    private double budget;
    private String reminder = "";
    private final ArrayList<Room> rooms = new ArrayList<>();

    public Project(int id, String name, String description, double budget) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.budget = budget;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
    public String getReminder() { return reminder; }
    public void setReminder(String reminder) { this.reminder = reminder; }
    public ArrayList<Room> getRooms() { return rooms; }

    public Room getRoomById(int roomId) {
        for (Room room : rooms) {
            if (room.getId() == roomId) {
                return room;
            }
        }
        return null;
    }

    public double getTotalCost() {
        double total = 0;
        for (Room room : rooms) {
            total += room.getCost();
        }
        return total;
    }

    @Override
    public String toString() {
        return name;
    }
}