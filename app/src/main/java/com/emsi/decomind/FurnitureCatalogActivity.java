/*
 * Projet : DecoMind
 * Fichier : FurnitureCatalogActivity.java
 * Description : Catalogue de meubles : recherche par nom, filtres par catégorie et par prix
 * Responsable(s) :
 * - El Guettafi Soukaina
 */

package com.emsi.decomind;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.emsi.decomind.adapter.FurnitureAdapter;
import com.emsi.decomind.data.DataStore;
import com.emsi.decomind.model.Furniture;
import com.emsi.decomind.model.Project;
import com.emsi.decomind.model.Room;

import java.util.ArrayList;
import java.util.Locale;

public class FurnitureCatalogActivity extends AppCompatActivity {

    private Project project;
    private Room room;
    private final ArrayList<Furniture> shown = new ArrayList<>();
    private FurnitureAdapter adapter;
    private EditText etSearch;
    private Spinner spCategory;
    private Spinner spPrice;
    private TextView tvCount;
    private TextView tvRoom;

    // Récupère la quantité choisie dans FurnitureDetailActivity
    private final ActivityResultLauncher<Intent> detailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    Furniture furniture = DataStore.getFurniture(data.getIntExtra("furniture_id", -1));
                    int quantity = data.getIntExtra("quantity", 1);
                    if (furniture != null && room != null) {
                        DataStore.addSelectedItem(room, furniture, quantity);
                        Toast.makeText(this, getString(R.string.item_added, quantity,
                                furniture.getName(), room.getName()), Toast.LENGTH_SHORT).show();
                        updateRoomInfo();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_catalog);
        DataStore.init(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        project = DataStore.getProject(getIntent().getIntExtra("project_id", -1));
        if (project != null) {
            room = project.getRoomById(getIntent().getIntExtra("room_id", -1));
        }
        if (project == null || room == null) {
            finish();
            return;
        }

        tvRoom = findViewById(R.id.tv_room);
        etSearch = findViewById(R.id.et_search);
        spCategory = findViewById(R.id.sp_category);
        spPrice = findViewById(R.id.sp_price);
        tvCount = findViewById(R.id.tv_count);
        ListView listFurniture = findViewById(R.id.list_furniture);

        adapter = new FurnitureAdapter(this, shown);
        listFurniture.setAdapter(adapter);

        // Sélection d'un meuble : ouverture de son détail
        listFurniture.setOnItemClickListener((parent, view, position, id) -> {
            Furniture furniture = shown.get(position);
            Intent intent = new Intent(this, FurnitureDetailActivity.class);
            intent.putExtra("project_id", project.getId());
            intent.putExtra("room_id", room.getId());
            intent.putExtra("furniture_id", furniture.getId());
            detailLauncher.launch(intent);
        });

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.categories_filter, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> priceAdapter = ArrayAdapter.createFromResource(
                this, R.array.price_filter, android.R.layout.simple_spinner_item);
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrice.setAdapter(priceAdapter);

        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spCategory.setOnItemSelectedListener(filterListener);
        spPrice.setOnItemSelectedListener(filterListener);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                applyFilters();
            }
        });

        updateRoomInfo();
        applyFilters();
    }

    private void updateRoomInfo() {
        tvRoom.setText(getString(R.string.catalog_room_info, room.getName(), room.getItemsCount()));
    }

    // Recherche par nom + filtre par catégorie + filtre par prix
    private void applyFilters() {
        String query = etSearch.getText().toString().trim().toLowerCase(Locale.FRANCE);
        int categoryPos = spCategory.getSelectedItemPosition();
        String category = String.valueOf(spCategory.getSelectedItem());
        int pricePos = spPrice.getSelectedItemPosition();

        shown.clear();
        for (Furniture f : DataStore.catalog) {
            boolean okName = query.isEmpty() || f.getName().toLowerCase(Locale.FRANCE).contains(query);
            boolean okCategory = categoryPos <= 0 || f.getCategory().equals(category);
            boolean okPrice = pricePos <= 0
                    || (pricePos == 1 && f.getPrice() < 1000)
                    || (pricePos == 2 && f.getPrice() >= 1000 && f.getPrice() <= 3000)
                    || (pricePos == 3 && f.getPrice() > 3000);
            if (okName && okCategory && okPrice) {
                shown.add(f);
            }
        }
        adapter.notifyDataSetChanged();
        tvCount.setText(getString(R.string.results_count, shown.size()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}