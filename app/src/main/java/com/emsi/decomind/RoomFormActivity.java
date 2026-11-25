/*
 * Projet : DecoMind
 * Fichier : RoomFormActivity.java
 * Description : Formulaire de création et de modification d'une pièce (dimensions, photo depuis la galerie)
 * Responsable(s) :
 * - Lahroum Amina
 */

package com.emsi.decomind;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.emsi.decomind.data.DataStore;
import com.emsi.decomind.model.Project;
import com.emsi.decomind.model.Room;

public class RoomFormActivity extends AppCompatActivity {

    private static final String STATE_PHOTO = "photo_uri";

    private Project project;
    private Room room;
    private boolean editMode = false;
    private Spinner spType;
    private EditText etName;
    private EditText etLength;
    private EditText etWidth;
    private EditText etHeight;
    private TextView tvArea;
    private ImageView ivPhoto;
    private String photoUri = "";

    // Intent implicite : ouverture de la galerie pour choisir une image
    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    photoUri = uri.toString();
                    showPhoto();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_form);
        DataStore.init(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        project = DataStore.getProject(getIntent().getIntExtra("project_id", -1));
        if (project == null) {
            finish();
            return;
        }

        spType = findViewById(R.id.sp_type);
        etName = findViewById(R.id.et_name);
        etLength = findViewById(R.id.et_length);
        etWidth = findViewById(R.id.et_width);
        etHeight = findViewById(R.id.et_height);
        tvArea = findViewById(R.id.tv_area);
        ivPhoto = findViewById(R.id.iv_photo);
        Button btnPhoto = findViewById(R.id.btn_add_photo);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnCancel = findViewById(R.id.btn_cancel);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.room_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);

        // Mode transmis par ProjectDetailActivity : "create" ou "edit"
        String mode = getIntent().getStringExtra("mode");
        editMode = "edit".equals(mode);

        if (editMode) {
            room = project.getRoomById(getIntent().getIntExtra("room_id", -1));
            if (room == null) {
                finish();
                return;
            }
            setTitle(R.string.title_edit_room);
            etName.setText(room.getName());
            etLength.setText(String.valueOf(room.getLength()));
            etWidth.setText(String.valueOf(room.getWidth()));
            etHeight.setText(String.valueOf(room.getHeight()));
            photoUri = room.getPhotoUri();
            for (int i = 0; i < typeAdapter.getCount(); i++) {
                if (String.valueOf(typeAdapter.getItem(i)).equals(room.getType())) {
                    spType.setSelection(i);
                }
            }
        } else {
            setTitle(R.string.title_new_room);
        }

        // Conserve la photo choisie lors d'un changement d'orientation
        if (savedInstanceState != null) {
            photoUri = savedInstanceState.getString(STATE_PHOTO, "");
        }
        showPhoto();

        // Calcul automatique de la superficie
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateArea();
            }
        };
        etLength.addTextChangedListener(watcher);
        etWidth.addTextChangedListener(watcher);
        updateArea();

        btnPhoto.setOnClickListener(v -> pickImage.launch("image/*"));
        btnSave.setOnClickListener(v -> saveRoom());
        btnCancel.setOnClickListener(v -> finish());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_PHOTO, photoUri);
    }

    private void showPhoto() {
        if (photoUri == null || photoUri.isEmpty()) {
            ivPhoto.setImageResource(R.drawable.ic_image);
        } else {
            ivPhoto.setImageURI(Uri.parse(photoUri));
        }
    }

    private double parse(EditText editText) {
        try {
            return Double.parseDouble(editText.getText().toString().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void updateArea() {
        double length = parse(etLength);
        double width = parse(etWidth);
        double area = (length > 0 && width > 0) ? length * width : 0;
        tvArea.setText(getString(R.string.area_label, DataStore.formatNumber(area)));
    }

    private void saveRoom() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError(getString(R.string.error_room_name));
            return;
        }
        double length = parse(etLength);
        double width = parse(etWidth);
        double height = parse(etHeight);
        if (length <= 0 || width <= 0 || height <= 0) {
            Toast.makeText(this, R.string.error_dimensions, Toast.LENGTH_SHORT).show();
            return;
        }
        String type = spType.getSelectedItem().toString();

        if (editMode) {
            room.setType(type);
            room.setName(name);
            room.setLength(length);
            room.setWidth(width);
            room.setHeight(height);
            room.setPhotoUri(photoUri);
        } else {
            Room newRoom = new Room(DataStore.newRoomId(), type, name, length, width, height);
            newRoom.setPhotoUri(photoUri);
            project.getRooms().add(newRoom);
        }
        Toast.makeText(this, R.string.room_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}