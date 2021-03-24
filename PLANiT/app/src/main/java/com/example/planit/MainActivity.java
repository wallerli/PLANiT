package com.example.planit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    public static String VIEW_PROJECT_ID = "com.example.planit.VIEW_PROJECT_ID";
    public static String VIEW_TASK_ID = "com.example.planit.VIEW_TASK_ID";
    Globals globals = null;
    FloatingActionButton fab = null;
    FloatingActionButton fab_close = null;

    boolean fab_expanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        globals = Globals.getInstance();

        fab = findViewById(R.id.fab);
        fab.animate().setDuration(200);
//        fab_close = findViewById(R.id.fab_close);
//        fab_close.animate().setDuration(200);

//        fab.setOnClickListener(view -> {
//            fab.setExpanded(true);
//            fab_close.setVisibility(View.VISIBLE);
//            fab.setVisibility(View.INVISIBLE);
//        });
//        fab_close.setOnClickListener(view -> {
//            fab.setExpanded(false);
//            fab_close.setVisibility(View.INVISIBLE);
//            fab.setVisibility(View.VISIBLE);
//        });

        fab.setOnClickListener(view -> {
            fab_expanded = !fab_expanded;
            rotateFab();
            fab.setExpanded(fab_expanded);
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
        fab_expanded = false;
        rotateFab();
        fab.setExpanded(fab_expanded);
    }

    public void openEditTask(View view){
        Intent intent = new Intent(this, EditTaskActivity.class);
        //intent.putExtra(); pass array of contact in the future
        startActivity(intent);
        fab_expanded = false;
        rotateFab();
        fab.setExpanded(fab_expanded);
    }

    public void rotateFab() {
        this.fab.animate().setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .rotation(fab_expanded ? 45f : 0f);
    }
}