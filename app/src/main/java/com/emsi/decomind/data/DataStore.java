/*
 * Projet : DecoMind
 * Fichier : DataStore.java
 * Description : Données fictives du projet (projets, catalogue, palettes) et méthodes utilitaires
 * Responsable(s) :
 * - Aouifia Doha
 * - Lahroum Amina
 * - El Guettafi Soukaina
 * - Gamran Nour El Houda
 */

package com.emsi.decomind.data;

import android.content.Context;

import com.emsi.decomind.R;
import com.emsi.decomind.model.ColorOption;
import com.emsi.decomind.model.Furniture;
import com.emsi.decomind.model.Palette;
import com.emsi.decomind.model.Project;
import com.emsi.decomind.model.Room;
import com.emsi.decomind.model.SelectedItem;

import java.util.ArrayList;
import java.util.Locale;

public class DataStore {

    public static final ArrayList<Project> projects = new ArrayList<>();
    public static final ArrayList<Furniture> catalog = new ArrayList<>();
    public static final ArrayList<Palette> palettes = new ArrayList<>();

    private static int nextProjectId = 1;
    private static int nextRoomId = 1;
    private static boolean initialized = false;

    public static void init(Context context) {
        if (initialized) {
            return;
        }
        initialized = true;

        // ----- Catalogue fictif -----
        catalog.add(new Furniture(1, "Canapé 3 places", "Canapés", 4500,
                "Canapé confortable en tissu, trois places, idéal pour un salon moderne.", R.drawable.ic_sofa));
        catalog.add(new Furniture(2, "Canapé d'angle", "Canapés", 7800,
                "Grand canapé d'angle modulable pour les salons spacieux.", R.drawable.ic_sofa));
        catalog.add(new Furniture(3, "Table basse en bois", "Tables", 900,
                "Table basse en bois massif avec finition naturelle.", R.drawable.ic_table));
        catalog.add(new Furniture(4, "Table à manger 6 personnes", "Tables", 2800,
                "Table rectangulaire en chêne pour six personnes.", R.drawable.ic_table));
        catalog.add(new Furniture(5, "Chaise scandinave", "Chaises", 450,
                "Chaise légère au style scandinave, pieds en bois.", R.drawable.ic_chair));
        catalog.add(new Furniture(6, "Fauteuil club", "Chaises", 1900,
                "Fauteuil club en cuir pour un coin lecture élégant.", R.drawable.ic_chair));
        catalog.add(new Furniture(7, "Lampadaire arc", "Luminaires", 600,
                "Lampadaire à arc avec abat-jour en tissu.", R.drawable.ic_lamp));
        catalog.add(new Furniture(8, "Suspension industrielle", "Luminaires", 750,
                "Suspension en métal noir au style industriel.", R.drawable.ic_lamp));
        catalog.add(new Furniture(9, "Miroir mural rond", "Décoration", 350,
                "Miroir rond avec cadre doré, diamètre 80 cm.", R.drawable.ic_decor));
        catalog.add(new Furniture(10, "Tapis berbère", "Décoration", 1200,
                "Tapis berbère tissé à la main, motifs géométriques.", R.drawable.ic_decor));
        catalog.add(new Furniture(11, "Bibliothèque en chêne", "Rangement", 2300,
                "Bibliothèque à cinq étagères en chêne clair.", R.drawable.ic_storage));
        catalog.add(new Furniture(12, "Commode 4 tiroirs", "Rangement", 3200,
                "Commode spacieuse à quatre tiroirs avec poignées en laiton.", R.drawable.ic_storage));

        // ----- Palettes de couleurs fictives -----
        Palette earth = new Palette(context.getString(R.string.palette_earth));
        earth.getColors().add(new ColorOption(context.getString(R.string.color_sand), context.getColor(R.color.c_sand)));
        earth.getColors().add(new ColorOption(context.getString(R.string.color_terracotta), context.getColor(R.color.c_terracotta)));
        earth.getColors().add(new ColorOption(context.getString(R.string.color_ecru), context.getColor(R.color.c_ecru)));
        earth.getColors().add(new ColorOption(context.getString(R.string.color_brown), context.getColor(R.color.c_brown)));
        palettes.add(earth);

        Palette nordic = new Palette(context.getString(R.string.palette_nordic));
        nordic.getColors().add(new ColorOption(context.getString(R.string.color_night_blue), context.getColor(R.color.c_night_blue)));
        nordic.getColors().add(new ColorOption(context.getString(R.string.color_blue_grey), context.getColor(R.color.c_blue_grey)));
        nordic.getColors().add(new ColorOption(context.getString(R.string.color_ice_white), context.getColor(R.color.c_ice_white)));
        nordic.getColors().add(new ColorOption(context.getString(R.string.color_turquoise), context.getColor(R.color.c_turquoise)));
        palettes.add(nordic);

        Palette sage = new Palette(context.getString(R.string.palette_sage));
        sage.getColors().add(new ColorOption(context.getString(R.string.color_sage), context.getColor(R.color.c_sage)));
        sage.getColors().add(new ColorOption(context.getString(R.string.color_light_green), context.getColor(R.color.c_light_green)));
        sage.getColors().add(new ColorOption(context.getString(R.string.color_cream), context.getColor(R.color.c_cream)));
        sage.getColors().add(new ColorOption(context.getString(R.string.color_forest), context.getColor(R.color.c_forest)));
        palettes.add(sage);

        Palette orient = new Palette(context.getString(R.string.palette_orient));
        orient.getColors().add(new ColorOption(context.getString(R.string.color_saffron), context.getColor(R.color.c_saffron)));
        orient.getColors().add(new ColorOption(context.getString(R.string.color_spice_red), context.getColor(R.color.c_spice_red)));
        orient.getColors().add(new ColorOption(context.getString(R.string.color_majorelle), context.getColor(R.color.c_majorelle)));
        orient.getColors().add(new ColorOption(context.getString(R.string.color_ivory), context.getColor(R.color.c_ivory)));
        palettes.add(orient);

        // ----- Projets fictifs -----
        Project casa = new Project(newProjectId(), "Appartement Casablanca",
                "Rénovation complète de l'appartement familial", 30000);
        Room salon = new Room(newRoomId(), "Salon", "Salon principal", 5.0, 4.0, 2.7);
        salon.setStyle(context.getString(R.string.style_modern));
        salon.getColors().add(earth.getColors().get(0));
        salon.getColors().add(earth.getColors().get(1));
        addSelectedItem(salon, getFurniture(1), 1);
        addSelectedItem(salon, getFurniture(3), 1);
        addSelectedItem(salon, getFurniture(7), 2);
        casa.getRooms().add(salon);
        Room chambre = new Room(newRoomId(), "Chambre", "Chambre parentale", 4.0, 3.5, 2.6);
        chambre.setStyle(context.getString(R.string.style_minimalist));
        chambre.getColors().add(nordic.getColors().get(2));
        addSelectedItem(chambre, getFurniture(12), 1);
        addSelectedItem(chambre, getFurniture(8), 1);
        casa.getRooms().add(chambre);
        projects.add(casa);

        Project villa = new Project(newProjectId(), "Villa Marrakech",
                "Décoration de la villa de vacances", 12000);
        Room cuisine = new Room(newRoomId(), "Cuisine", "Cuisine ouverte", 3.5, 3.0, 2.7);
        addSelectedItem(cuisine, getFurniture(5), 4);
        villa.getRooms().add(cuisine);
        projects.add(villa);
    }

    public static int newProjectId() {
        return nextProjectId++;
    }

    public static int newRoomId() {
        return nextRoomId++;
    }

    public static Project getProject(int id) {
        for (Project p : projects) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public static Furniture getFurniture(int id) {
        for (Furniture f : catalog) {
            if (f.getId() == id) {
                return f;
            }
        }
        return null;
    }

    public static ColorOption findColor(String name) {
        for (Palette palette : palettes) {
            for (ColorOption color : palette.getColors()) {
                if (color.getName().equals(name)) {
                    return color;
                }
            }
        }
        return null;
    }

    public static void addSelectedItem(Room room, Furniture furniture, int quantity) {
        for (SelectedItem item : room.getItems()) {
            if (item.getFurniture().getId() == furniture.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        room.getItems().add(new SelectedItem(furniture, quantity));
    }

    public static String joinColors(Room room) {
        StringBuilder sb = new StringBuilder();
        for (ColorOption color : room.getColors()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(color.getName());
        }
        return sb.toString();
    }

    public static String formatMoney(double value) {
        return String.format(Locale.FRANCE, "%.0f", value);
    }

    public static String formatNumber(double value) {
        return String.format(Locale.FRANCE, "%.1f", value);
    }
}