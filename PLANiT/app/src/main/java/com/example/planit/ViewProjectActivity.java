package com.example.planit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ViewProjectActivity extends AppCompatActivity {

    public static String EDIT_PROJECT_ID = "com.example.planit.EDIT_PROJECT_ID";

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
        toolbar.inflateMenu(R.menu.menu_toolbar_view);

        toolbar.setNavigationOnClickListener(view -> finish());

        Intent intent = getIntent();
        UUID project_id = UUID.fromString(intent.getStringExtra(MainActivity.VIEW_PROJECT_ID));
        project = Globals.getInstance().getProject(project_id);
        tasks.addAll(project.getTasks());

        title = findViewById(R.id.projectTitleTextView);
        due = findViewById(R.id.projectDueTextView);
        text = findViewById(R.id.projectDescriptionTextView);

        title.setText(project.getTitle());
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.getDefault());
        if (project.getDueDate() != null)
            due.setText(df.format(project.getDueDate()));
        text.setText(project.getText());

        recyclerView = (RecyclerView) findViewById(R.id.tasksRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TaskAdapter adapter = new TaskAdapter(this, tasks);
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

            Intent intent = new Intent(this, EditProjectActivity.class);
            intent.putExtra(EDIT_PROJECT_ID, project.getUUID().toString());
            startActivity(intent);
            return true;
        }

        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }
}
