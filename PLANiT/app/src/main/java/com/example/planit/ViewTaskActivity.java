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
    UUID task_id;
    Globals globals = Globals.getInstance();

    Task task;
    TextView title, projectTitle, text;
    RecyclerView recyclerView;
    List<UUID> blockers = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
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

        Intent intent = getIntent();
        task_id = UUID.fromString(intent.getStringExtra(MainActivity.VIEW_TASK_ID));

        title = findViewById(R.id.taskTitleTextView);
        projectTitle = findViewById(R.id.taskProjectTitleTextView);
        text = findViewById(R.id.taskDescriptionTextView);
        recyclerView = findViewById(R.id.tasksRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        populate();
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
    private void populate() {
        task = globals.getTask(task_id);

        title.setText(task.getTitle());
        projectTitle.setText(globals.getParentProject(task.getUUID()).getTitle());
        text.setText(task.getText());

        TaskAdapter adapter = new TaskAdapter(this, task.getOrderedBlockers());
        recyclerView.setAdapter(adapter);
    }
}