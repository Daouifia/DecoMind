/*
 * Projet : DecoMind
 * Fichier : ProjectDetailActivity.java
 * Description : Fiche d'un projet : pièces, actions, rappel (date et heure) et cycle de vie
 * Responsable(s) :
 * - Lahroum Amina
 */

package com.emsi.decomind;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.emsi.decomind.data.DataStore;
import com.emsi.decomind.model.Project;
import com.emsi.decomind.model.Room;

import java.util.Calendar;
import java.util.Locale;

public class ProjectDetailActivity extends AppCompatActivity {

    // Étiquette à filtrer dans Logcat pour suivre le cycle de vie
    private static final String TAG = "DecoMind-Cycle";
    private static final String STATE_SELECTED = "selected_pos";

    private Project project;
    private ArrayAdapter<Room> adapter;
    private ListView listRooms;
    private TextView tvDescription;
    private TextView tvReminder;
    private TextView tvRoomInfo;
    private int selectedPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ProjectDetailActivity : onCreate");
        setContentView(R.layout.activity_project_detail);
        DataStore.init(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Données transmises par MainActivity
        int projectId = getIntent().getIntExtra("project_id", -1);
        String projectName = getIntent().getStringExtra("project_name");
        project = DataStore.getProject(projectId);
        if (project == null) {
            finish();
            return;
        }
        if (projectName != null) {
            setTitle(projectName);
        }
        // Retrouve la pièce sélectionnée après un changement d'orientation
        if (savedInstanceState != null) {
            selectedPos = savedInstanceState.getInt(STATE_SELECTED, -1);
        }

        tvDescription = findViewById(R.id.tv_description);
        tvReminder = findViewById(R.id.tv_reminder);
        tvRoomInfo = findViewById(R.id.tv_room_info);
        listRooms = findViewById(R.id.list_rooms);
        Button btnAddRoom = findViewById(R.id.btn_add_room);
        Button btnEditRoom = findViewById(R.id.btn_edit_room);
        Button btnDeleteRoom = findViewById(R.id.btn_delete_room);
        Button btnFurniture = findViewById(R.id.btn_furniture);
        Button btnDecoration = findViewById(R.id.btn_decoration);
        Button btnBudget = findViewById(R.id.btn_budget);

        adapter = new ArrayAdapter<Room>(this, android.R.layout.simple_list_item_single_choice,
                project.getRooms()) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Room room = getItem(position);
                if (room != null) {
                    ((TextView) view).setText(ProjectDetailActivity.this.getString(R.string.room_line,
                            room.getName(), room.getType(), DataStore.formatNumber(room.getArea())));
                }
                return view;
            }
        };
        listRooms.setAdapter(adapter);
        listRooms.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listRooms.setOnItemClickListener((parent, view, position, id) -> {
            selectedPos = position;
            showRoomInfo();
        });

        btnAddRoom.setOnClickListener(v -> {
            Intent intent = new Intent(this, RoomFormActivity.class);
            intent.putExtra("mode", "create");
            intent.putExtra("project_id", project.getId());
            startActivity(intent);
        });

        btnEditRoom.setOnClickListener(v -> {
            Room room = selectedRoom();
            if (room != null) {
                Intent intent = new Intent(this, RoomFormActivity.class);
                intent.putExtra("mode", "edit");
                intent.putExtra("project_id", project.getId());
                intent.putExtra("room_id", room.getId());
                startActivity(intent);
            }
        });

        btnDeleteRoom.setOnClickListener(v -> {
            Room room = selectedRoom();
            if (room != null) {
                confirmDeleteRoom(room);
            }
        });

        btnFurniture.setOnClickListener(v -> {
            Room room = selectedRoom();
            if (room != null) {
                Intent intent = new Intent(this, FurnitureCatalogActivity.class);
                intent.putExtra("project_id", project.getId());
                intent.putExtra("room_id", room.getId());
                startActivity(intent);
            }
        });

        btnDecoration.setOnClickListener(v -> {
            Room room = selectedRoom();
            if (room != null) {
                Intent intent = new Intent(this, DecorationActivity.class);
                intent.putExtra("project_id", project.getId());
                intent.putExtra("room_id", room.getId());
                startActivity(intent);
            }
        });

        btnBudget.setOnClickListener(v -> {
            Intent intent = new Intent(this, BudgetSummaryActivity.class);
            intent.putExtra("project_id", project.getId());
            startActivity(intent);
        });
    }

    private Room selectedRoom() {
        if (selectedPos >= 0 && selectedPos < project.getRooms().size()) {
            return project.getRooms().get(selectedPos);
        }
        Toast.makeText(this, R.string.select_room_first, Toast.LENGTH_SHORT).show();
        return null;
    }

    private void refresh() {
        setTitle(project.getName());
        tvDescription.setText(project.getDescription());
        if (project.getReminder().isEmpty()) {
            tvReminder.setText(R.string.no_reminder);
        } else {
            tvReminder.setText(getString(R.string.reminder_label, project.getReminder()));
        }
        if (selectedPos >= project.getRooms().size()) {
            selectedPos = -1;
            listRooms.clearChoices();
        }
        adapter.notifyDataSetChanged();
        if (selectedPos >= 0) {
            listRooms.setItemChecked(selectedPos, true);
        }
        showRoomInfo();
    }

    private void showRoomInfo() {
        if (selectedPos < 0 || selectedPos >= project.getRooms().size()) {
            tvRoomInfo.setText(R.string.select_room_hint);
            return;
        }
        Room room = project.getRooms().get(selectedPos);
        String style = room.getStyle().isEmpty() ? getString(R.string.none_value) : room.getStyle();
        String colors = DataStore.joinColors(room);
        if (colors.isEmpty()) {
            colors = getString(R.string.none_value);
        }
        tvRoomInfo.setText(getString(R.string.room_info, room.getType(),
                DataStore.formatNumber(room.getLength()),
                DataStore.formatNumber(room.getWidth()),
                DataStore.formatNumber(room.getHeight()),
                DataStore.formatNumber(room.getArea()),
                style, colors, room.getItemsCount()));
    }

    private void confirmDeleteRoom(Room room) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_room_title)
                .setMessage(getString(R.string.delete_room_message, room.getName()))
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    project.getRooms().remove(room);
                    selectedPos = -1;
                    listRooms.clearChoices();
                    refresh();
                    Toast.makeText(this, R.string.room_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void confirmDeleteProject() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_project_title)
                .setMessage(getString(R.string.delete_project_message, project.getName()))
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    DataStore.projects.remove(project);
                    Toast.makeText(this, R.string.project_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    // Rappel : choix de la date (DatePickerDialog) puis de l'heure (TimePickerDialog)
    private void pickReminder() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (dateView, year, month, day) ->
                new TimePickerDialog(this, (timeView, hour, minute) -> {
                    project.setReminder(String.format(Locale.FRANCE, "%02d/%02d/%d %02d:%02d",
                            day, month + 1, year, hour, minute));
                    refresh();
                    Toast.makeText(this, R.string.reminder_saved, Toast.LENGTH_SHORT).show();
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show(),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_project) {
            Intent intent = new Intent(this, ProjectFormActivity.class);
            intent.putExtra("mode", "edit");
            intent.putExtra("project_id", project.getId());
            startActivity(intent);
            return true;
        } else if (id == R.id.action_set_reminder) {
            pickReminder();
            return true;
        } else if (id == R.id.action_delete_project) {
            confirmDeleteProject();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED, selectedPos);
    }

    // ---------- Cycle de vie de l'activité (voir Logcat, filtre : DecoMind-Cycle) ----------

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "ProjectDetailActivity : onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "ProjectDetailActivity : onResume");
        if (project != null) {
            refresh();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "ProjectDetailActivity : onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "ProjectDetailActivity : onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ProjectDetailActivity : onDestroy");
    }
}