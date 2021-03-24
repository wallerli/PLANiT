package com.example.planit;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    public static String VIEW_PROJECT_ID = "com.example.planit.VIEW_PROJECT_ID";
    public static String VIEW_TASK_ID = "com.example.planit.VIEW_TASK_ID";
    Globals globals = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        globals = Globals.getInstance();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (fab.isExpanded())
                fab.setRotation(0);
            else
                fab.setRotation(135f);
            fab.setExpanded(!fab.isExpanded());
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openAProject(View view) {
        Intent intent = new Intent(this, ViewProjectActivity.class);
        intent.putExtra(VIEW_PROJECT_ID, globals.getProject());
        startActivity(intent);
    }

    public void openATask(View view) {
        Intent intent = new Intent(this, ViewTaskActivity.class);
        intent.putExtra(VIEW_TASK_ID, globals.getTask());
        startActivity(intent);
    }

    public void openEditProject(View view){
        Intent intent = new Intent(this, EditProjectActivity.class);
        //intent.putExtra(); pass array of contact in the future
        startActivity(intent);
    }

    public void openEditTask(View view){
        Intent intent = new Intent(this, EditTaskActivity.class);
        //intent.putExtra(); pass array of contact in the future
        startActivity(intent);
    }
}