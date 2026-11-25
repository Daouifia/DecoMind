/*
 * Projet : DecoMind
 * Fichier : FurnitureAdapter.java
 * Description : Adaptateur (ArrayAdapter) affichant un meuble du catalogue avec image, nom, catégorie et prix
 * Responsable(s) :
 * - El Guettafi Soukaina
 */

package com.emsi.decomind.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.emsi.decomind.R;
import com.emsi.decomind.data.DataStore;
import com.emsi.decomind.model.Furniture;

import java.util.List;

public class FurnitureAdapter extends ArrayAdapter<Furniture> {

    private final LayoutInflater inflater;

    public FurnitureAdapter(Context context, List<Furniture> items) {
        super(context, 0, items);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = inflater.inflate(R.layout.item_furniture, parent, false);
        }
        Furniture furniture = getItem(position);
        if (furniture != null) {
            ImageView image = row.findViewById(R.id.iv_item);
            TextView name = row.findViewById(R.id.tv_item_name);
            TextView category = row.findViewById(R.id.tv_item_category);
            TextView price = row.findViewById(R.id.tv_item_price);
            image.setImageResource(furniture.getImageRes());
            name.setText(furniture.getName());
            category.setText(furniture.getCategory());
            price.setText(getContext().getString(R.string.price_format,
                    DataStore.formatMoney(furniture.getPrice())));
        }
        return row;
    }
}