/*
 * Projet : DecoMind
 * Fichier : FurnitureDetailActivity.java
 * Description : Détail d'un meuble : image, description, prix et choix de la quantité
 * Responsable(s) :
 * - El Guettafi Soukaina
 */

package com.emsi.decomind;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.emsi.decomind.data.DataStore;
import com.emsi.decomind.model.Furniture;

import java.util.ArrayList;

public class FurnitureDetailActivity extends AppCompatActivity {

    private Furniture furniture;
    private Spinner spQuantity;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_detail);
        DataStore.init(this);
        setTitle(R.string.title_furniture_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        furniture = DataStore.getFurniture(getIntent().getIntExtra("furniture_id", -1));
        if (furniture == null) {
            finish();
            return;
        }

        ImageView ivFurniture = findViewById(R.id.iv_furniture);
        TextView tvBadge = findViewById(R.id.tv_price_badge);
        TextView tvName = findViewById(R.id.tv_name);
        TextView tvCategory = findViewById(R.id.tv_category);
        TextView tvDescription = findViewById(R.id.tv_description);
        spQuantity = findViewById(R.id.sp_quantity);
        tvTotal = findViewById(R.id.tv_total);
        Button btnAdd = findViewById(R.id.btn_add_to_room);
        Button btnCancel = findViewById(R.id.btn_cancel);

        ivFurniture.setImageResource(furniture.getImageRes());
        tvBadge.setText(getString(R.string.price_format, DataStore.formatMoney(furniture.getPrice())));
        tvName.setText(furniture.getName());
        tvCategory.setText(furniture.getCategory());
        tvDescription.setText(furniture.getDescription());

        ArrayList<Integer> quantities = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            quantities.add(i);
        }
        ArrayAdapter<Integer> quantityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, quantities);
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spQuantity.setAdapter(quantityAdapter);
        spQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Retour vers le catalogue avec la quantité choisie
        btnAdd.setOnClickListener(v -> {
            Intent result = new Intent();
            result.putExtra("project_id", getIntent().getIntExtra("project_id", -1));
            result.putExtra("room_id", getIntent().getIntExtra("room_id", -1));
            result.putExtra("furniture_id", furniture.getId());
            result.putExtra("quantity", selectedQuantity());
            setResult(RESULT_OK, result);
            finish();
        });
        btnCancel.setOnClickListener(v -> finish());

        updateTotal();
    }

    private int selectedQuantity() {
        Object selected = spQuantity.getSelectedItem();
        return selected == null ? 1 : (Integer) selected;
    }

    private void updateTotal() {
        tvTotal.setText(getString(R.string.total_label,
                DataStore.formatMoney(furniture.getPrice() * selectedQuantity())));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}