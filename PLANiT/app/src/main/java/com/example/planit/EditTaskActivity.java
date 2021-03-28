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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.Map;

import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.UUID;

import static com.example.planit.MainActivity.VIEW_TASK_ID;

public class EditTaskActivity extends AppCompatActivity {

    public static String EDIT_TASK_ID = "com.example.planit.EDIT_TASK_ID";
    public static String PARENT_PROJECT_ID = "com.example.planit.PARENT_PROJECT_ID";
    Globals globals = Globals.getInstance();

    Task task;
    Project parentProject;
    TextInputEditText titleEdit, textEdit;
    AutoCompleteTextView act_projects;
    ChipGroup sizeChips;
    ChipGroup priorityChips;
    ChipGroup tagChips;
    TextView emptyTagsText;

    final Integer[] sizeChipIDs = new Integer[] {R.id.tiny_chip, R.id.small_chip, R.id.medium_chip, R.id.large_chip, R.id.huge_chip};
    final Integer[] priorityChipIDs = new Integer[] {R.id.low_chip, R.id.moderate_chip, R.id.high_chip, R.id.critical_chip};

    // Project menu
    ArrayList<String> arrayList_project = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter_project;

    boolean newTask = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initializing xml structure
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        Toolbar toolbar = findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_toolbar_edit);

        // Filling content
        Intent intent = getIntent();

        titleEdit = findViewById(R.id.task_title_text);
        act_projects = findViewById(R.id.act_projects);
        sizeChips = findViewById(R.id.size_chips);
        priorityChips = findViewById(R.id.priority_chips);
        tagChips = findViewById(R.id.tag_chips);
        textEdit = findViewById(R.id.edit_description);
        emptyTagsText = findViewById(R.id.empty_tags_text);

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
            newTask = true;
        }
        titleEdit.setText(task.getTitle());

        String strUUID = intent.getStringExtra(PARENT_PROJECT_ID);
        if (strUUID != null) {
            parentProject = globals.getProject(UUID.fromString(strUUID));
            act_projects.setText(parentProject.getTitle());
        }

        // Get all project names to fill in menu
        for (Map.Entry<UUID, Project> p : globals.getProjects().entrySet()) {
            arrayList_project.add(p.getValue().getTitle());
        }
        arrayAdapter_project = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, arrayList_project);
        act_projects.setAdapter(arrayAdapter_project);
        act_projects.setThreshold(4); // characters required to load suggestion for spinner

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

        act_projects.setOnItemClickListener((parent, view, position, id) -> {
            parentProject = (Project) globals.getProjects().values().toArray()[position];
        });

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

        if (task.getTags().size() == 0)
            emptyTagsText.setVisibility(View.VISIBLE);
        else
            emptyTagsText.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        globals.addTask(task);
        parentProject.addTask(task.getUUID());
        globals.addProject(parentProject);
        if (item.getItemId() == R.id.action_done) {
            finish();
            if (newTask) {
                Intent intent = new Intent(this, ViewTaskActivity.class);
                intent.putExtra(VIEW_TASK_ID, task.getUUID().toString());
                startActivity(intent);
            }
        }

        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }
}
