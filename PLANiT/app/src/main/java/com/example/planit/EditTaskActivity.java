package com.example.planit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.UUID;

public class EditTaskActivity extends AppCompatActivity {

    Task task = null;
    Globals globals = null;

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