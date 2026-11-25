/*
 * Projet : DecoMind
 * Fichier : BudgetSummaryActivity.java
 * Description : Budget du projet, récapitulatif des meubles et de la décoration, partage
 * Responsable(s) :
 * - Gamran Nour El Houda
 */

package com.emsi.decomind;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.emsi.decomind.data.DataStore;
import com.emsi.decomind.model.Project;
import com.emsi.decomind.model.Room;
import com.emsi.decomind.model.SelectedItem;

import java.util.ArrayList;

public class BudgetSummaryActivity extends AppCompatActivity {

    // Une ligne de la liste : un meuble sélectionné dans une pièce
    private static class BudgetLine {
        final Room room;
        final SelectedItem item;

        BudgetLine(Room room, SelectedItem item) {
            this.room = room;
            this.item = item;
        }
    }

    private static final String STATE_SELECTED = "selected_pos";

    private Project project;
    private final ArrayList<BudgetLine> lines = new ArrayList<>();
    private ArrayAdapter<BudgetLine> adapter;
    private ListView listLines;
    private TextView tvBudget;
    private TextView tvSpent;
    private TextView tvRemaining;
    private TextView tvRecap;
    private EditText etBudget;
    private int selectedPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_summary);
        DataStore.init(this);
        setTitle(R.string.title_budget);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        project = DataStore.getProject(getIntent().getIntExtra("project_id", -1));
        if (project == null) {
            finish();
            return;
        }

        if (savedInstanceState != null) {
            selectedPos = savedInstanceState.getInt(STATE_SELECTED, -1);
        }

        tvBudget = findViewById(R.id.tv_budget);
        tvSpent = findViewById(R.id.tv_spent);
        tvRemaining = findViewById(R.id.tv_remaining);
        tvRecap = findViewById(R.id.tv_recap);
        etBudget = findViewById(R.id.et_budget);
        listLines = findViewById(R.id.list_lines);
        Button btnUpdate = findViewById(R.id.btn_update_budget);
        Button btnMinus = findViewById(R.id.btn_minus);
        Button btnPlus = findViewById(R.id.btn_plus);
        Button btnRemove = findViewById(R.id.btn_remove);
        Button btnEdit = findViewById(R.id.btn_edit_furniture);
        Button btnShare = findViewById(R.id.btn_share);
        Button btnBack = findViewById(R.id.btn_back_project);

        adapter = new ArrayAdapter<BudgetLine>(this, android.R.layout.simple_list_item_single_choice,
                lines) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                BudgetLine line = getItem(position);
                if (line != null) {
                    ((TextView) view).setText(BudgetSummaryActivity.this.getString(R.string.budget_line,
                            line.room.getName(), line.item.getFurniture().getName(),
                            line.item.getQuantity(), DataStore.formatMoney(line.item.getTotal())));
                }
                return view;
            }
        };
        listLines.setAdapter(adapter);
        listLines.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listLines.setOnItemClickListener((parent, view, position, id) -> selectedPos = position);

        btnUpdate.setOnClickListener(v -> updateBudget());

        btnPlus.setOnClickListener(v -> {
            BudgetLine line = selectedLine();
            if (line != null) {
                line.item.setQuantity(line.item.getQuantity() + 1);
                refresh(true);
            }
        });

        btnMinus.setOnClickListener(v -> {
            BudgetLine line = selectedLine();
            if (line != null && line.item.getQuantity() > 1) {
                line.item.setQuantity(line.item.getQuantity() - 1);
                refresh(true);
            }
        });

        btnRemove.setOnClickListener(v -> {
            BudgetLine line = selectedLine();
            if (line != null) {
                line.room.getItems().remove(line.item);
                selectedPos = -1;
                listLines.clearChoices();
                refresh(false);
                Toast.makeText(this, R.string.item_removed, Toast.LENGTH_SHORT).show();
            }
        });

        // Retour vers le catalogue de la pièce concernée
        btnEdit.setOnClickListener(v -> {
            Room room = null;
            if (selectedPos >= 0 && selectedPos < lines.size()) {
                room = lines.get(selectedPos).room;
            } else if (!project.getRooms().isEmpty()) {
                room = project.getRooms().get(0);
            }
            if (room == null) {
                Toast.makeText(this, R.string.no_room_yet, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, FurnitureCatalogActivity.class);
            intent.putExtra("project_id", project.getId());
            intent.putExtra("room_id", room.getId());
            startActivity(intent);
        });

        // Intent implicite : partage du récapitulatif avec une autre application
        btnShare.setOnClickListener(v -> {
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject, project.getName()));
            send.putExtra(Intent.EXTRA_TEXT, buildShareText());
            startActivity(Intent.createChooser(send, getString(R.string.share_chooser)));
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (project != null) {
            refresh(false);
        }
    }

    private BudgetLine selectedLine() {
        if (selectedPos >= 0 && selectedPos < lines.size()) {
            return lines.get(selectedPos);
        }
        Toast.makeText(this, R.string.select_line_first, Toast.LENGTH_SHORT).show();
        return null;
    }

    private void rebuildLines() {
        lines.clear();
        for (Room room : project.getRooms()) {
            for (SelectedItem item : room.getItems()) {
                lines.add(new BudgetLine(room, item));
            }
        }
        if (selectedPos >= lines.size()) {
            selectedPos = -1;
            listLines.clearChoices();
        }
        adapter.notifyDataSetChanged();
        if (selectedPos >= 0) {
            listLines.setItemChecked(selectedPos, true);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED, selectedPos);
    }

    // Recalcule la liste, les totaux et le récapitulatif ; signale un éventuel dépassement
    private void refresh(boolean alertIfOver) {
        rebuildLines();
        double spent = project.getTotalCost();
        double remaining = project.getBudget() - spent;

        tvBudget.setText(getString(R.string.budget_total, DataStore.formatMoney(project.getBudget())));
        tvSpent.setText(getString(R.string.budget_spent, DataStore.formatMoney(spent)));
        tvRemaining.setText(getString(R.string.budget_remaining, DataStore.formatMoney(remaining)));
        tvRemaining.setTextColor(ContextCompat.getColor(this,
                remaining < 0 ? R.color.error_red : R.color.success_green));
        tvRecap.setText(buildRecap());

        if (alertIfOver && remaining < 0) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.over_budget_title)
                    .setMessage(getString(R.string.over_budget_message, DataStore.formatMoney(-remaining)))
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    }

    private void updateBudget() {
        try {
            double value = Double.parseDouble(etBudget.getText().toString().trim().replace(',', '.'));
            if (value < 0) {
                throw new NumberFormatException();
            }
            project.setBudget(value);
            etBudget.setText("");
            refresh(true);
            Toast.makeText(this, R.string.budget_updated, Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_budget, Toast.LENGTH_SHORT).show();
        }
    }

    private String buildRecap() {
        if (project.getRooms().isEmpty()) {
            return getString(R.string.recap_empty);
        }
        StringBuilder sb = new StringBuilder();
        for (Room room : project.getRooms()) {
            String style = room.getStyle().isEmpty() ? getString(R.string.none_value) : room.getStyle();
            String colors = DataStore.joinColors(room);
            if (colors.isEmpty()) {
                colors = getString(R.string.none_value);
            }
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(getString(R.string.recap_room, room.getName(), style, colors));
        }
        return sb.toString();
    }

    private String buildShareText() {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.share_title, project.getName())).append("\n\n");
        for (Room room : project.getRooms()) {
            String style = room.getStyle().isEmpty() ? getString(R.string.none_value) : room.getStyle();
            String colors = DataStore.joinColors(room);
            if (colors.isEmpty()) {
                colors = getString(R.string.none_value);
            }
            sb.append(getString(R.string.recap_room, room.getName(), style, colors)).append("\n");
            for (SelectedItem item : room.getItems()) {
                sb.append("  - ").append(getString(R.string.share_item,
                        item.getFurniture().getName(), item.getQuantity(),
                        DataStore.formatMoney(item.getTotal()))).append("\n");
            }
        }
        double spent = project.getTotalCost();
        sb.append("\n")
                .append(getString(R.string.budget_total, DataStore.formatMoney(project.getBudget()))).append("\n")
                .append(getString(R.string.budget_spent, DataStore.formatMoney(spent))).append("\n")
                .append(getString(R.string.budget_remaining,
                        DataStore.formatMoney(project.getBudget() - spent)));
        return sb.toString();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}