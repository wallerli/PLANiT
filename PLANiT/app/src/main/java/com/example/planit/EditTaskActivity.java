package com.example.planit;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.Map;

import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.UUID;

import static android.view.View.GONE;
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
    Button delete;

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
        delete = findViewById(R.id.delete_button);

        if (intent.getStringExtra(EDIT_TASK_ID) != null) {
            task = new Task(globals.getTask(UUID.fromString(intent.getStringExtra(EDIT_TASK_ID))));
            toolbar.setTitle("");
            textEdit.setText(task.getText());
            titleEdit.setText(task.getTitle());
        }
        else {
            task = new Task("");
            toolbar.setTitle("Add New Task");
            delete.setVisibility(GONE);
            newTask = true;
            titleEdit.requestFocus();
        }
        setSupportActionBar(toolbar);
        sizeChips.check(sizeChipIDs[task.getSize().ordinal()]);
        priorityChips.check(priorityChipIDs[task.getPriority().ordinal()]);

        if (intent.getStringExtra(PARENT_PROJECT_ID) != null) {
            parentProject = globals.getProject(UUID.fromString(intent.getStringExtra(PARENT_PROJECT_ID)));
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
        updateChips();
//        if (task.getTags().size() == 0)
//            emptyTagsText.setVisibility(View.VISIBLE);
//        else
//            emptyTagsText.setVisibility(View.INVISIBLE);

        delete.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Delete \"" + task.getTitle() + "\"?");
            alertDialog.setMessage("Are you sure you want to delete this task?\n\nThis action cannot be undone!");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE",
                    (dialog, which) -> {
                        globals.removeTask(task.getUUID());
                        dialog.dismiss();
                        finish();
                    });
            alertDialog.show();
            Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (b != null) {
                TypedValue typedValue = new TypedValue();
                getTheme().resolveAttribute(R.attr.colorError, typedValue, true);
                @ColorInt int color = typedValue.data;
                b.setTextColor(color);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_edit, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            int v1 = Task.validateTitle(task.getTitle());
            int v2 = Task.validateText(task.getText());
            if (v1 != 0 || parentProject == null) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Please complete all required fields");
                String message = "";
                if (v1 == 1)
                    message += "* The title cannot be empty.\n";
                if (v1 == 2)
                    message += "* This title is too long. Make sure your title is under " + Globals.MAX_TITLE_LENGTH + " characters.\n";
                if (parentProject == null)
                    message += "* Must select a project to assign this task to so it doesn't get lost.\n";
                if (v2 == 2)
                    message += "* The description is too long. Make sure your description is under " + Globals.MAX_TEXT_LENGTH + " characters.\n";
                alertDialog.setMessage(message);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DISMISS",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            }
            else {
                globals.addTask(task);
                parentProject.addTask(task.getUUID());
                globals.addProject(parentProject);
                finish();
                if (newTask) {
                    Intent intent = new Intent(this, ViewTaskActivity.class);
                    intent.putExtra(VIEW_TASK_ID, task.getUUID().toString());
                    startActivity(intent);
                }
            }
        }

        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateChips() {
        tagChips.removeAllViews();
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
            lChip.setCloseIconVisible(true);
            tagChips.addView(lChip);
        });
        Chip lChip = new Chip(this);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;
        lChip.setTextColor(color);
        lChip.setText(R.string.create_tag);
        tagChips.addView(lChip);
    }
}
