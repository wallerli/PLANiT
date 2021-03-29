package com.example.planit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.planit.MainActivity.VIEW_PROJECT_ID;

public class ViewTaskActivity extends AppCompatActivity {

    public static String EDIT_TASK_ID = "com.example.planit.EDIT_TASK_ID";

    UUID task_id;
    Task task;
    TextView title, projectTitle, text, emptyRecyclerText, indicatorText;
    RecyclerView recyclerView;
    List<UUID> blockers = new ArrayList<>();
    List<UUID> tags = new ArrayList<>();
    CircularProgressIndicator indicator;
    ChipGroup tagChips;
    Chip sizeChip, priorityChip;
    boolean completed;
    boolean unblocked;
    int completeThickness;
    int incompleteThickness;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        populate();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        populate();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        Toolbar toolbar = findViewById(R.id.view_toolbar);
        toolbar.setTitle("View Task");
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_toolbar_view);

        toolbar.setNavigationOnClickListener(view -> finish());
        completeThickness = (int) getResources().getDimension(R.dimen.task_indicator_complete_thickness_large);
        incompleteThickness = (int) getResources().getDimension(R.dimen.task_indicator_incomplete_thickness_large);

        Intent intent = getIntent();
        task_id = UUID.fromString(intent.getStringExtra(MainActivity.VIEW_TASK_ID));

        title = findViewById(R.id.taskTitleTextView);
        projectTitle = findViewById(R.id.taskProjectTitleTextView);
        text = findViewById(R.id.taskDescriptionTextView);
        emptyRecyclerText = findViewById(R.id.empty_recycler_text);
        tagChips = findViewById(R.id.taskTags);
        indicator = findViewById(R.id.task_indicator);
        sizeChip = findViewById(R.id.sizeChip);
        priorityChip = findViewById(R.id.priorityChip);
        indicatorText = findViewById(R.id.task_indicator_text);
        recyclerView = findViewById(R.id.tasksRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        populate();

        indicator.setOnClickListener(v -> {
            int ret;
            if (completed) {
                ret = Globals.getInstance().setTaskCompleted(task_id, false);
                if (ret == 0) {
                    completed = false;
                    setIncomplete();
                } else if (ret == 3) {
                    completed = false;
                    unblocked = false;
                    setBlocked();
                }
            } else {
                ret = Globals.getInstance().setTaskCompleted(task_id, true);
                if (ret == 0) {
                    completed = true;
                    setComplete();
                } else if (ret == 2) {
                    completed = false;
                    unblocked = false;
                    setBlocked();
                    AlertDialog alertDialog = new AlertDialog.Builder(ViewTaskActivity.this).create();
                    alertDialog.setTitle("The Task is Blocked");
                    alertDialog.setMessage("Please complete all its blockers before marking the task completed.");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DISMISS",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                }
            }
            populate();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(this, EditTaskActivity.class);
            intent.putExtra(EDIT_TASK_ID, task.getUUID().toString());
            startActivity(intent);
            return true;
        }

        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void populate() {
        Globals globals = Globals.getInstance();
        task = globals.getTask(task_id);
        if (task == null) {
            finish();
            return;
        }
        blockers = task.getOrderedBlockers();
        tags = task.getTags();
        title.setText(task.getTitle());
        projectTitle.setText(globals.getParentProject(task.getUUID()).getTitle());
        projectTitle.setOnClickListener(v -> {
            Intent intentNew = new Intent(this, ViewProjectActivity.class);
            intentNew.putExtra(VIEW_PROJECT_ID, Globals.getInstance().getParentProject(task_id).getUUID().toString());
            startActivity(intentNew);
        });
        text.setText(task.getText());
        updateChips();
        recyclerView.setAdapter(new TaskAdapter(this, task.getOrderedBlockers()));
        if (task.getOrderedBlockers().size() == 0)
            emptyRecyclerText.setVisibility(View.VISIBLE);
        else
            emptyRecyclerText.setVisibility(View.INVISIBLE);
        updateCompleteness();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateCompleteness() {
        Globals globals = Globals.getInstance();
        task = globals.getTask(task_id);
        completed = task.getCompleteStatus();
        if (completed) {
            setComplete();
        } else if (task.getBlockers().stream().allMatch(b -> globals.getTask(b).getCompleteStatus())) {
            setIncomplete();
        } else {
            setBlocked();
        }
    }

    public void setComplete() {
        indicator.setProgress(100);
        indicator.setTrackThickness(completeThickness);
        indicatorText.setText(R.string.done_period);
    }

    public void setIncomplete() {
        indicator.setProgress(100);
        indicator.setTrackThickness(incompleteThickness);
        indicatorText.setText(R.string.done_question_mark);
    }

    public void setBlocked() {
        indicator.setProgress(0);
        indicator.setTrackThickness(incompleteThickness);
        indicatorText.setText(R.string.blocked);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateChips() {
        Globals globals = Globals.getInstance();
        String taskSize = task.getSize().toString();
        sizeChip.setText(String.format("%s%s", taskSize.charAt(0), taskSize.substring(1).toLowerCase()));
        sizeChip.setChecked(true);
        String taskPriority = task.getPriority().toString();
        priorityChip.setText(String.format("%s%s", taskPriority.charAt(0), taskPriority.substring(1).toLowerCase()));
        priorityChip.setChecked(true);
        tagChips.removeAllViews();
        tags.forEach(t -> {
            Tag tag = globals.getTag(t);
            Chip lChip = new Chip(this);
            lChip.setText(tag.getName());
            if (tag.getHexColor() != -1) {
                lChip.setTextColor(getResources().getColor(R.color.white));
                lChip.setChipBackgroundColor(ColorStateList.valueOf(tag.getHexColor()));
            }
            lChip.setEnsureMinTouchTargetSize(false);
            lChip.setClickable(false);
            lChip.setFocusable(false);
            tagChips.addView(lChip);
        });
    }
}