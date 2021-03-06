package com.morphedstudio.planit;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import static android.view.View.GONE;
import static com.morphedstudio.planit.MainActivity.VIEW_TASK_ID;

public class EditTaskActivity extends AppCompatActivity {

    public static String EDIT_TASK_ID = "com.morphedstudio.planit.EDIT_TASK_ID";
    public static String PARENT_PROJECT_ID = "com.morphedstudio.planit.PARENT_PROJECT_ID";
    Globals globals = Globals.getInstance();

    Task task;
    Project parentProject;
    TextInputEditText titleEdit, textEdit;
    TextInputLayout titleInput, textInput, parentInput;
    AutoCompleteTextView act_projects;
    ChipGroup sizeChips;
    ChipGroup priorityChips;
    ChipGroup tagChips;
    TextView emptyTagsText, emptyBlockersText, blockersLabel;
    RecyclerView blockersRecycler;
    Button delete, createProject;

    ArrayList<String> arrayList_project = new ArrayList<>();
    final Integer[] sizeChipIDs = new Integer[] {R.id.tiny_chip, R.id.small_chip, R.id.medium_chip, R.id.large_chip, R.id.huge_chip};
    final Integer[] priorityChipIDs = new Integer[] {R.id.low_chip, R.id.moderate_chip, R.id.high_chip, R.id.critical_chip};

    boolean newTask = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRestart() {
        super.onRestart();
        initializeActProjects();
    }

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
        emptyBlockersText = findViewById(R.id.empty_blockers_text);
        blockersRecycler = findViewById(R.id.blockers);
        blockersLabel = findViewById(R.id.blockers_label);
        blockersRecycler.setHasFixedSize(true);
        blockersRecycler.setLayoutManager(new LinearLayoutManager(this));
        blockersRecycler.setNestedScrollingEnabled(false);
        createProject = findViewById(R.id.new_project_button);
        delete = findViewById(R.id.delete_button);
        titleInput = findViewById(R.id.edit_task_title);
        textInput = findViewById(R.id.descriptionText);
        parentInput = findViewById(R.id.til_project);
        titleInput.setCounterMaxLength(Globals.MAX_TITLE_LENGTH);
        textInput.setCounterMaxLength(Globals.MAX_TEXT_LENGTH);

        if (intent.getStringExtra(EDIT_TASK_ID) != null) {
            task = new Task(globals.getTask(UUID.fromString(intent.getStringExtra(EDIT_TASK_ID))));
            parentProject = new Project(globals.getParentProject(task.getUUID()));
            toolbar.setTitle("Edit Task");
            textEdit.setText(task.getText());
            titleEdit.setText(task.getTitle());
        }
        else {
            task = new Task("");
            if (intent.getStringExtra(PARENT_PROJECT_ID) != null) {
                parentProject = new Project(globals.getProject(UUID.fromString(intent.getStringExtra(PARENT_PROJECT_ID))));
            }
            toolbar.setTitle(R.string.create_task);
            delete.setVisibility(GONE);
            newTask = true;
            titleEdit.requestFocus();
        }
        setSupportActionBar(toolbar);
        Globals.updateToolbarColor(this, toolbar);
        sizeChips.check(sizeChipIDs[task.getSize().ordinal()]);
        priorityChips.check(priorityChipIDs[task.getPriority().ordinal()]);
        updateTagChips();
        resetBlockersList();

        if (parentProject != null) {
            act_projects.setText(parentProject.getTitle());
        }
        arrayList_project = Globals.getInstance().getProjects().values().stream().map(Project::getTitle).collect(Collectors.toCollection(ArrayList::new));

        initializeActProjects();

        // Activating listeners
        toolbar.setNavigationOnClickListener(view -> finish());

        titleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch (Task.validateTitle(s.toString())) {
                    case 1:
                        titleInput.setError("The title cannot be empty.");
                        break;
                    case 2:
                        titleInput.setError("This title is too long.");
                        break;
                    default:
                        titleInput.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                task.setTitle(s.toString());
            }
        });

        titleEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                switch (Task.validateTitle(task.getTitle())) {
                    case 1:
                        titleInput.setError("The title cannot be empty.");
                        break;
                    case 2:
                        titleInput.setError("This title is too long.");
                        break;
                    default:
                        titleInput.setError(null);
                }
            }
        });

        act_projects.setOnItemClickListener((parent, view, position, id) -> {
            parentProject = new Project((Project) globals.getProjects().values().toArray()[arrayList_project.indexOf(act_projects.getAdapter().getItem(position))]);
            act_projects.setText(parentProject.getTitle());
            task.removeAllBlockers();
            resetBlockersList();
            parentInput.setError(null);
        });

        act_projects.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (act_projects.getText().toString().length() == 0)
                    parentInput.setError("Select a project to assign this task to.");
            }
        });

        act_projects.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (act_projects.getText().toString().length() == 0) {
                    parentInput.setError("Select a project to assign this task to.");
                    parentProject = null;
                    resetBlockersList();
                }
                else if (parentProject == null || !act_projects.getText().toString().equals(parentProject.getTitle())) {
                    parentInput.setError("Select a project from the drop down menu.");
                    parentProject = null;
                    resetBlockersList();
                }
                else
                    parentInput.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (act_projects.getAdapter().getCount() == 0)
                    parentInput.setError("No matched project.");
                else
                    parentInput.setError(null);
            }
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
                switch (Task.validateText(s.toString())) {
                    case 2:
                        textInput.setError("This description is too long.");
                        break;
                    default:
                        textInput.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                task.setText(s.toString());
            }
        });

        textEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                switch (Task.validateText(task.getText())) {
                    case 2:
                        textInput.setError("This description is too long.");
                        break;
                    default:
                        textInput.setError(null);
                }
            }
        });

        delete.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Delete \"" + task.getTitle() + "\"?");
            alertDialog.setMessage("Are you sure you want to delete this task?\n\nThis action cannot be undone!");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE",
                    (dialog, which) -> {
                        globals.removeTask(task.getUUID());
                        if (globals.save(this) == 0 && Globals.getInstance().getTask(task.getUUID()) == null) {
                            finish();
                            Toast.makeText(getApplicationContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.try_again, Toast.LENGTH_SHORT).show();
                        }
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

        blockersLabel.setOnClickListener(v -> {AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("What will happen if a task is blocked?");
            alertDialog.setMessage("When a task is blocked, it cannot be completed until all other tasks in the \"Blocked by\" list are completed first.\n\n" +
                    "Tasks can only be blocked by other tasks from the same project. Tasks blocked by the current task are hidden.");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DISMISS",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.show();
        });

        createProject.setOnClickListener(v -> {
            Intent intent_project = new Intent(this, EditProjectActivity.class);
            intent_project.putExtra(EDIT_TASK_ID, task.getUUID().toString());
            startActivity(intent_project);
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
            if (v1 != 0 || parentProject == null || !act_projects.getText().toString().equals(parentProject.getTitle())) {
                int problemCode = -1;
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Please complete all required fields");
                String message = "";
                if (v1 == 1) {
                    message += "* The title cannot be empty.\n";
                    problemCode = 1;
                }
                if (v1 == 2) {
                    message += "* This title is too long. Make sure your title is under " + Globals.MAX_TITLE_LENGTH + " characters.\n";
                    problemCode = 1;
                }
                if (parentProject == null || !act_projects.getText().toString().equals(parentProject.getTitle())) {
                    message += "* Must select a project to assign this task to so it doesn't get lost.\n";
                    if (problemCode == -1) problemCode = 2;
                }
                if (v2 == 2) {
                    message += "* The description is too long. Make sure your description is under " + Globals.MAX_TEXT_LENGTH + " characters.\n";
                    if (problemCode == -1) problemCode = 3;
                }
                alertDialog.setMessage(message);
                int finalProblemCode = problemCode;
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DISMISS",
                        (dialog, which) -> {
                            dialog.dismiss();
                            switch (finalProblemCode) {
                                case 1:
                                    titleEdit.requestFocus();
                                    break;
                                case 2:
                                    act_projects.requestFocus();
                                    break;
                                case 3:
                                    textEdit.requestFocus();
                                    break;
                            }
                        });
                alertDialog.show();
            }
            else {
                if (!newTask) {
                    globals.getParentProject(task.getUUID()).removeTask(task.getUUID());
                }
                globals.addTask(task);
                parentProject.addTask(task.getUUID());
                globals.addProject(parentProject);
                if (globals.save(this) == 0) {
                    finish();
                    if (newTask) {
                        Intent intent = new Intent(this, ViewTaskActivity.class);
                        intent.putExtra(VIEW_TASK_ID, task.getUUID().toString());
                        startActivity(intent);
                    }
                    Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_SHORT).show();
                    globals.save(this);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            }
        }

        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateTagChips() {
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
            lChip.setOnCloseIconClickListener(v ->
                    Toast.makeText(getApplicationContext(),R.string.to_be_implemented,Toast.LENGTH_SHORT).show());
            tagChips.addView(lChip);
        });
        Chip lChip = new Chip(this);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;
        lChip.setTextColor(color);
        lChip.setText(R.string.create_tag);
        lChip.setOnClickListener(v ->
                Toast.makeText(getApplicationContext(),R.string.to_be_implemented,Toast.LENGTH_SHORT).show());
        tagChips.addView(lChip);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void resetBlockersList() {
        List<UUID> blockers =
                (parentProject != null) ?
                        (parentProject.containsTask(task.getUUID()) ?
                                globals.getValidBlockers(task.getUUID()) :
                                parentProject.getTasks().stream().map(globals::getTask).sorted((t1, t2) ->
                                        t1.getTitle().compareTo(t2.getTitle())).map(Task::getUUID).collect(Collectors.toList())
                        ) :
                        new ArrayList<>();
        if (blockers.size() == 0)
            emptyBlockersText.setVisibility(View.VISIBLE);
        else
            emptyBlockersText.setVisibility(View.INVISIBLE);
        blockersRecycler.setAdapter(new BlockerAdapter(this, blockers, task));
    }

    /**
     * Get all project names to fill in menu
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initializeActProjects() {
        ArrayList<String> arrayList_project_new = Globals.getInstance().getProjects().values().stream().map(Project::getTitle).collect(Collectors.toCollection(ArrayList::new));
        String newProjectTitle = arrayList_project_new.stream().filter(p -> !arrayList_project.contains(p)).findFirst().orElse(null);
        arrayList_project = arrayList_project_new;
        act_projects.setAdapter(
                Globals.isNightMode(this)
                        ? new ArrayAdapter<>(getApplicationContext(), R.layout.custom_autocomplete_night, arrayList_project)
                        : new ArrayAdapter<>(getApplicationContext(), R.layout.custom_autocomplete, arrayList_project)
        );
        act_projects.setThreshold(0);
        if (newProjectTitle != null) {
            act_projects.setText(newProjectTitle);
            globals.getProjects().values().stream().filter(p -> p.getTitle().equals(newProjectTitle)).findFirst().ifPresent(project -> parentProject = new Project(project));
        }
    }
}
