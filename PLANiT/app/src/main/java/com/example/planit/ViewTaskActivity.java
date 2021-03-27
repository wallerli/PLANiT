package com.example.planit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ViewTaskActivity extends AppCompatActivity {

    public static String EDIT_TASK_ID = "com.example.planit.EDIT_TASK_ID";

    Task task;
    UUID task_id;
    TextView title, projectTitle, text;
    RecyclerView recyclerView;
    List<UUID> blockers = new ArrayList<>();
    CircularProgressIndicator indicator;
    boolean completed;
    boolean unblocked;
    int completeThickness;
    int incompleteThickness;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        updateTask();
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
        indicator = findViewById(R.id.task_indicator);
        updateTask();

        recyclerView = findViewById(R.id.tasksRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TaskAdapter adapter = new TaskAdapter(this, blockers);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

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
            updateTask();
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
    public void updateTask() {
        Globals globals = Globals.getInstance();
        task = globals.getTask(task_id);
        blockers = task.getOrderedBlockers();
        title.setText(task.getTitle());
        projectTitle.setText(globals.getParentProject(task.getUUID()).getTitle());
        text.setText(task.getText());
        completed = task.getCompleteStatus();
        unblocked = task.getBlockers().stream().allMatch(b -> globals.getTask(b).getCompleteStatus());
        if (completed) {
            setComplete();
        } else if (unblocked) {
            setIncomplete();
        } else {
            setBlocked();
        }
    }

    public void setComplete() {
        indicator.setProgress(100);
        indicator.setTrackThickness(completeThickness);
    }

    public void setIncomplete() {
        indicator.setProgress(100);
        indicator.setTrackThickness(incompleteThickness);
    }

    public void setBlocked() {
        indicator.setProgress(0);
        indicator.setTrackThickness(incompleteThickness);
    }
}