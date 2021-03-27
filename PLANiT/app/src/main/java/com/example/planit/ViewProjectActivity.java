package com.example.planit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ViewProjectActivity extends AppCompatActivity {

    public static String EDIT_PROJECT_ID = "com.example.planit.EDIT_PROJECT_ID";

    Project project;
    TextView title, due, text, completenessText;
    RecyclerView recyclerView;
    UUID projectUUID;
    List<UUID> tasks = new ArrayList<>();
    CircularProgressIndicator indicator;
    ColorStateList defaultDueDateColor;

    @Override
    public void onResume() {
        super.onResume();
        updateProject();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_project);
        Toolbar toolbar = findViewById(R.id.view_toolbar);
        toolbar.setTitle("View Project");
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_toolbar_view);

        toolbar.setNavigationOnClickListener(view -> finish());

        Intent intent = getIntent();
        projectUUID = UUID.fromString(intent.getStringExtra(MainActivity.VIEW_PROJECT_ID));

        title = findViewById(R.id.projectTitleTextView);
        due = findViewById(R.id.projectDueTextView);
        text = findViewById(R.id.projectDescriptionTextView);
        completenessText = findViewById(R.id.project_indicator_text);
        indicator = findViewById(R.id.project_indicator);
        defaultDueDateColor =  completenessText.getTextColors();
        updateProject();

        recyclerView = findViewById(R.id.tasksRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TaskAdapter adapter = new TaskAdapter(this, tasks);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

        indicator.setOnClickListener(v -> updateProject());
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

    public void updateProject() {
        Globals globals = Globals.getInstance();
        project = globals.getProject(projectUUID);
        tasks = project.getOrderedTasks();
        title.setText(project.getTitle());
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault());
        Date dd = project.getDueDate();
        if (dd != null) {
            this.due.setText(df.format(project.getDueDate()));
            if (dd.getTime() < System.currentTimeMillis() && project.getCompleteness() < 1) {
                due.setTextColor(getResources().getColor(R.color.orange_700));
                due.setTypeface(null, Typeface.BOLD);
            } else {
                due.setTextColor(defaultDueDateColor);
                due.setTypeface(null);
            }
        } else {
            due.setText(R.string.no_due_date);
        }
        text.setText(project.getText());
        completenessText.setText(String.format(Locale.getDefault(), "%.1f%%", 100 * project.getCompleteness()));
        indicator.setProgress((int) (100 * project.getCompleteness()));
    }
}
