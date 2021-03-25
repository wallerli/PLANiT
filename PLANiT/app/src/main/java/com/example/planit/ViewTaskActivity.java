package com.example.planit;

import androidx.annotation.RequiresApi;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ViewTaskActivity extends AppCompatActivity {

    public static String EDIT_TASK_ID = "com.example.planit.EDIT_TASK_ID";

    Task task;
    TextView title, projectTitle, text;
    RecyclerView recyclerView;
    List<UUID> blockers = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        Toolbar toolbar = findViewById(R.id.view_toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_toolbar_view);

        toolbar.setNavigationOnClickListener(view -> finish());

        Intent intent = getIntent();
        UUID task_id = UUID.fromString(intent.getStringExtra(MainActivity.VIEW_TASK_ID));
        task = Globals.getInstance().getTask(task_id);
        blockers.addAll(task.getBlockers());

        title = findViewById(R.id.taskTitleTextView);
        projectTitle = findViewById(R.id.taskProjectTitleTextView);
        text = findViewById(R.id.taskDescriptionTextView);

        title.setText(task.getTitle());
        projectTitle.setText(Globals.getInstance().getParentProject(task.getUUID()).getTitle());
        text.setText(task.getText());

        recyclerView = (RecyclerView) findViewById(R.id.tasksRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TaskAdapter adapter = new TaskAdapter(this, blockers);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
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
}