/*
 * Projet : DecoMind
 * Fichier : ProjectFormActivity.java
 * Description : Formulaire de création et de modification d'un projet
 * Responsable(s) :
 * - Aouifia Doha
 */

package com.emsi.decomind;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.emsi.decomind.data.DataStore;
import com.emsi.decomind.model.Project;

public class ProjectFormActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etDescription;
    private Project project;
    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_form);
        DataStore.init(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etName = findViewById(R.id.et_name);
        etDescription = findViewById(R.id.et_description);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnCancel = findViewById(R.id.btn_cancel);

        // Mode transmis par l'activité appelante : "create" ou "edit"
        String mode = getIntent().getStringExtra("mode");
        editMode = "edit".equals(mode);

        if (editMode) {
            project = DataStore.getProject(getIntent().getIntExtra("project_id", -1));
            if (project == null) {
                finish();
                return;
            }
            etName.setText(project.getName());
            etDescription.setText(project.getDescription());
            setTitle(R.string.title_edit_project);
        } else {
            setTitle(R.string.title_new_project);
        }

        btnSave.setOnClickListener(v -> saveProject());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveProject() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError(getString(R.string.error_project_name));
            return;
        }
        if (editMode) {
            project.setName(name);
            project.setDescription(description);
        } else {
            DataStore.projects.add(new Project(DataStore.newProjectId(), name, description, 0));
        }
        Toast.makeText(this, R.string.project_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}