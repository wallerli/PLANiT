package com.example.planit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class EditTaskActivity extends AppCompatActivity {

    Task task = null;
    Globals globals = null;

    // Project menu
    TextInputLayout til_project = null;
    AutoCompleteTextView act_projects = null;
    ArrayList<String> arrayList_project = null;
    ArrayAdapter<String> arrayAdapter_project = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        Toolbar toolbar = findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_toolbar_edit);
        toolbar.setNavigationOnClickListener(view -> finish());

        globals = Globals.getInstance();
        Intent intent = getIntent();
        String strUUID = intent.getStringExtra(ViewTaskActivity.EDIT_TASK_ID);
        if (strUUID != null)
            task = globals.getTask(UUID.fromString(strUUID));

        // Project menu
        til_project = (TextInputLayout)findViewById(R.id.til_project);
        act_projects = (AutoCompleteTextView)findViewById(R.id.act_projects);
        arrayList_project = new ArrayList<>();

        // Get all project names to fill in menu
        Map<UUID, Project> projects = globals.getProjects();
        Iterator iterator = projects.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry projectIterator = (Map.Entry) iterator.next();
            Project temp = (Project) projectIterator.getValue();
            String projectName = temp.getTitle();
            arrayList_project.add(projectName);
        }
        arrayAdapter_project = new ArrayAdapter<>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,arrayList_project);
        act_projects.setAdapter(arrayAdapter_project);
        act_projects.setThreshold(4); // characters required to load suggestion for spinner
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            // User chose the "Settings" item, show the app settings UI...
            return true;
        }

        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }
}