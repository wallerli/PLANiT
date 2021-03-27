package com.example.planit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class EditTaskActivity extends AppCompatActivity {

    public static String EDIT_TASK_ID = "com.example.planit.EDIT_TASK_ID";
    Globals globals = Globals.getInstance();

    Task task;
    TextInputEditText titleEdit, textEdit;
    Spinner projects, blockers;
    ChipGroup sizeChips;
    ChipGroup priorityChips;
    ChipGroup tagChips;

//    ArrayList<UUID> projectUUIDs = (ArrayList<UUID>) globals.getProjects();
//    ArrayList<String> projectTitles = new ArrayList<>();
    final Integer[] sizeChipIDs = new Integer[] {R.id.tiny_chip, R.id.small_chip, R.id.medium_chip, R.id.large_chip, R.id.huge_chip};
    final Integer[] priorityChipIDs = new Integer[] {R.id.low_chip, R.id.moderate_chip, R.id.high_chip, R.id.critical_chip};

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initializing xml structure
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        Toolbar toolbar = findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_toolbar_edit);
        tagChips = findViewById(R.id.tag_chips);

//        for (UUID projectUUID : projectUUIDs) {
//            projectTitles.add(globals.getProject(projectUUID).getTitle());
//        }
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, projectTitles);
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        projects.setAdapter(arrayAdapter);

        // Filling content
        Intent intent = getIntent();

        titleEdit = findViewById(R.id.task_title_text);
        projects = findViewById(R.id.projects_spinner);
        sizeChips = findViewById(R.id.size_chips);
        priorityChips = findViewById(R.id.priority_chips);
        textEdit = findViewById(R.id.edit_description);

        if (intent.getStringExtra(EDIT_TASK_ID) != null) {
            task = new Task(globals.getTask(UUID.fromString(intent.getStringExtra(EDIT_TASK_ID))));
            toolbar.setTitle("Edit Task");
            sizeChips.check(sizeChipIDs[task.getSize().ordinal()]);
            priorityChips.check(priorityChipIDs[task.getPriority().ordinal()]);
            textEdit.setText(task.getText());
        }
        else {
            task = new Task("New Task");
            toolbar.setTitle("Add New Task");
        }
        titleEdit.setText(task.getTitle());

        // Activating listeners
        toolbar.setNavigationOnClickListener(view -> finish());

        titleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Add validation here
            }

            @Override
            public void afterTextChanged(Editable s) {
                task.setTitle(s.toString());
            }
        });

//        projects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                String projectTitle = parent.getItemAtPosition(position).toString();
//                Toast.makeText(parent.getContext(), "Selected: " + projectTitle, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView <?> parent) {
//            }
//        });

        sizeChips.setOnCheckedChangeListener((group, checkedId) ->
            task.setSize(Size.values()[Arrays.asList(sizeChipIDs).indexOf(checkedId)]));

        priorityChips.setOnCheckedChangeListener((group, checkedId) ->
            task.setPriority(Priority.values()[Arrays.asList(priorityChipIDs).indexOf(checkedId)]));

        textEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Add validation here
            }

            @Override
            public void afterTextChanged(Editable s) {
                task.setText(s.toString());
            }
        });

        task.getTags().forEach(t -> {
            Tag tag = globals.getTag(t);
            Chip lChip = new Chip(this);
            lChip.setText(tag.getName());
            if (tag.getHexColor() != -1) {
                lChip.setTextColor(getResources().getColor(R.color.white));
                lChip.setChipBackgroundColor(ColorStateList.valueOf(tag.getHexColor()));
            }
            lChip.setClickable(false);
            lChip.setFocusable(false);
            tagChips.addView(lChip);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            globals.addTask(task);
            finish();
        }

        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

}