package com.example.planit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ViewProjectActivity extends AppCompatActivity {

    Project project;
    TextView title, due, text;
    RecyclerView recyclerView;
    List<UUID> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_project);
        Toolbar toolbar = findViewById(R.id.view_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> finish());

        Intent intent = getIntent();
        UUID project_id = UUID.fromString(intent.getStringExtra(MainActivity.VIEW_PROJECT_ID));
        project = Globals.getInstance().getProject(project_id);
        if (project.getTasks() != null) tasks.addAll(project.getTasks());

        title = findViewById(R.id.projectTitleTextView);
        due = findViewById(R.id.projectDueTextView);
        text = findViewById(R.id.projectDescriptionTextView);

        title.setText(project.getTitle());
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.getDefault());
        due.setText(df.format(project.getDueDate()));
        text.setText(project.getText());

        recyclerView = (RecyclerView) findViewById(R.id.tasksRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TaskAdapter adapter = new TaskAdapter(this, tasks);
        recyclerView.setAdapter(adapter);
    }
}
