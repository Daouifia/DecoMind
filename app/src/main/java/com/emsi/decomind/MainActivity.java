/*
 * Projet : DecoMind
 * Fichier : MainActivity.java
 * Description : Interface d'accueil : liste des projets, tri (PopupMenu), menu d'options
 * Responsable(s) :
 * - Aouifia Doha
 */

package com.emsi.decomind;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.emsi.decomind.data.DataStore;
import com.emsi.decomind.model.Project;

import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<Project> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataStore.init(this);

        ListView listProjects = findViewById(R.id.list_projects);
        Button btnNew = findViewById(R.id.btn_new_project);
        Button btnSort = findViewById(R.id.btn_sort);

        adapter = new ArrayAdapter<Project>(this, android.R.layout.simple_list_item_2,
                android.R.id.text1, DataStore.projects) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Project p = getItem(position);
                TextView text2 = view.findViewById(android.R.id.text2);
                if (p != null) {
                    text2.setText(MainActivity.this.getString(R.string.project_line,
                            p.getRooms().size(), DataStore.formatMoney(p.getBudget())));
                }
                return view;
            }
        };
        listProjects.setAdapter(adapter);
        listProjects.setEmptyView(findViewById(R.id.empty_view));

        // Sélection d'un projet : ouverture de sa fiche (identifiant + nom transmis)
        listProjects.setOnItemClickListener((parent, view, position, id) -> {
            Project p = DataStore.projects.get(position);
            Intent intent = new Intent(MainActivity.this, ProjectDetailActivity.class);
            intent.putExtra("project_id", p.getId());
            intent.putExtra("project_name", p.getName());
            startActivity(intent);
        });

        // Appui long : suppression après confirmation
        listProjects.setOnItemLongClickListener((parent, view, position, id) -> {
            confirmDelete(DataStore.projects.get(position));
            return true;
        });

        btnNew.setOnClickListener(v -> openNewProject());
        btnSort.setOnClickListener(this::showSortMenu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void openNewProject() {
        Intent intent = new Intent(this, ProjectFormActivity.class);
        intent.putExtra("mode", "create");
        startActivity(intent);
    }

    private void showSortMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_sort, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.sort_name) {
                Collections.sort(DataStore.projects,
                        (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            } else if (id == R.id.sort_budget) {
                Collections.sort(DataStore.projects,
                        (a, b) -> Double.compare(a.getBudget(), b.getBudget()));
            }
            adapter.notifyDataSetChanged();
            return true;
        });
        popup.show();
    }

    private void confirmDelete(Project project) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_project_title)
                .setMessage(getString(R.string.delete_project_message, project.getName()))
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    DataStore.projects.remove(project);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, R.string.project_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_new_project) {
            openNewProject();
            return true;
        } else if (id == R.id.action_refresh) {
            adapter.notifyDataSetChanged();
            Toast.makeText(this, R.string.list_refreshed, Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_about) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.about_title)
                    .setMessage(R.string.about_message)
                    .setPositiveButton(R.string.ok, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}