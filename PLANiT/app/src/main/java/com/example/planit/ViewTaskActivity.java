package com.example.planit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ViewTaskActivity extends AppCompatActivity {

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
    }
}