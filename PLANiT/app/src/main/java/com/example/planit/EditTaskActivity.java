package com.example.planit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

public class EditTaskActivity extends AppCompatActivity {

    public static String EDIT_TASK_ID = "com.example.planit.EDIT_TASK_ID";

    Task task;
    Globals globals = Globals.getInstance();
    TextInputEditText title, text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        Toolbar toolbar = findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_toolbar_edit);

        toolbar.setNavigationOnClickListener(view -> finish());

        Intent intent = getIntent();
        if (intent.getStringExtra(EDIT_TASK_ID) != null) {
            task = globals.getTask(UUID.fromString(intent.getStringExtra(EDIT_TASK_ID)));
            toolbar.setTitle("Edit Task");
        }
        else {
            task = globals.getTask();
            toolbar.setTitle("Add New Task");
        }

        title = findViewById(R.id.task_title_text);
        text = findViewById(R.id.edit_description);

        title.setText(task.getTitle());
        text.setText(task.getText());
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