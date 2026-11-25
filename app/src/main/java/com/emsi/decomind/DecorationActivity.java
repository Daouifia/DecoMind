/*
 * Projet : DecoMind
 * Fichier : DecorationActivity.java
 * Description : Personnalisation de la décoration d'une pièce : style, palette et couleurs
 * Responsable(s) :
 * - Gamran Nour El Houda
 */

package com.emsi.decomind;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.emsi.decomind.data.DataStore;
import com.emsi.decomind.model.ColorOption;
import com.emsi.decomind.model.Palette;
import com.emsi.decomind.model.Project;
import com.emsi.decomind.model.Room;

import java.util.ArrayList;

public class DecorationActivity extends AppCompatActivity {

    private static final String STATE_COLORS = "chosen_colors";

    private Room room;
    private RadioGroup rgStyle;
    private LinearLayout llColors;
    private LinearLayout llPreview;
    // Noms des couleurs sélectionnées (toutes palettes confondues)
    private final ArrayList<String> chosen = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoration);
        DataStore.init(this);
        setTitle(R.string.title_decoration);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Project project = DataStore.getProject(getIntent().getIntExtra("project_id", -1));
        if (project != null) {
            room = project.getRoomById(getIntent().getIntExtra("room_id", -1));
        }
        if (room == null) {
            finish();
            return;
        }

        TextView tvRoom = findViewById(R.id.tv_room);
        rgStyle = findViewById(R.id.rg_style);
        Spinner spPalette = findViewById(R.id.sp_palette);
        llColors = findViewById(R.id.ll_colors);
        llPreview = findViewById(R.id.ll_preview);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnCancel = findViewById(R.id.btn_cancel);

        tvRoom.setText(getString(R.string.decoration_room, room.getName()));

        if (savedInstanceState != null) {
            ArrayList<String> saved = savedInstanceState.getStringArrayList(STATE_COLORS);
            if (saved != null) {
                chosen.addAll(saved);
            }
        } else {
            for (ColorOption color : room.getColors()) {
                chosen.add(color.getName());
            }
            selectStyle(room.getStyle());
        }

        ArrayAdapter<Palette> paletteAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, DataStore.palettes);
        paletteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPalette.setAdapter(paletteAdapter);
        spPalette.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showColors(DataStore.palettes.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        updatePreview();
        btnSave.setOnClickListener(v -> saveDecoration());
        btnCancel.setOnClickListener(v -> finish());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(STATE_COLORS, chosen);
    }

    private void selectStyle(String style) {
        for (int i = 0; i < rgStyle.getChildCount(); i++) {
            View child = rgStyle.getChildAt(i);
            if (child instanceof RadioButton
                    && ((RadioButton) child).getText().toString().equals(style)) {
                ((RadioButton) child).setChecked(true);
            }
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    // Petite pastille ronde de la couleur donnée
    private View createDot(int color) {
        View dot = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(32), dp(32));
        params.setMargins(dp(4), dp(4), dp(4), dp(4));
        dot.setLayoutParams(params);
        Drawable background = ContextCompat.getDrawable(this, R.drawable.bg_color_dot);
        if (background != null) {
            background = background.mutate();
            if (background instanceof GradientDrawable) {
                ((GradientDrawable) background).setColor(color);
            }
            dot.setBackground(background);
        }
        return dot;
    }

    // Affiche les couleurs de la palette avec une case à cocher par couleur
    private void showColors(Palette palette) {
        llColors.removeAllViews();
        for (ColorOption color : palette.getColors()) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);

            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(color.getName());
            checkBox.setChecked(chosen.contains(color.getName()));
            checkBox.setOnCheckedChangeListener((button, isChecked) -> {
                if (isChecked && !chosen.contains(color.getName())) {
                    chosen.add(color.getName());
                } else if (!isChecked) {
                    chosen.remove(color.getName());
                }
                updatePreview();
            });

            row.addView(createDot(color.getColor()));
            row.addView(checkBox);
            llColors.addView(row);
        }
    }

    private void updatePreview() {
        llPreview.removeAllViews();
        for (String name : chosen) {
            ColorOption color = DataStore.findColor(name);
            if (color != null) {
                llPreview.addView(createDot(color.getColor()));
            }
        }
    }

    private void saveDecoration() {
        RadioButton checked = findViewById(rgStyle.getCheckedRadioButtonId());
        if (checked == null) {
            Toast.makeText(this, R.string.error_style, Toast.LENGTH_SHORT).show();
            return;
        }
        room.setStyle(checked.getText().toString());
        room.getColors().clear();
        for (String name : chosen) {
            ColorOption color = DataStore.findColor(name);
            if (color != null) {
                room.getColors().add(color);
            }
        }
        Toast.makeText(this, R.string.decoration_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}