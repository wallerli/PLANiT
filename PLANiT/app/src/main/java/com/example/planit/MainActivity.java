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
import android.widget.SearchView;

public class MainActivity extends AppCompatActivity {

    public static String EDIT_PROJECT_ID = "com.example.planit.EDIT_PROJECT_ID";
    public static String EDIT_TASK_ID = "com.example.planit.EDIT_TASK_ID";
    public static String VIEW_PROJECT_ID = "com.example.planit.VIEW_PROJECT_ID";
    public static String VIEW_TASK_ID = "com.example.planit.VIEW_TASK_ID";
    Globals globals = Globals.getInstance();
    FloatingActionButton fab;
    boolean fab_expanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);

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
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        SearchView searchView = (android.widget.SearchView) searchItem.getActionView();
        searchView.onActionViewExpanded();

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                settingsItem.setVisible(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                settingsItem.setVisible(true);
                invalidateOptionsMenu();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_search) {
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
        intent.putExtra(EDIT_TASK_ID, globals.getTask()); // pass array of contact in the future
        startActivity(intent);
        fab_expanded = false;
        rotateFab();
        fab.setExpanded(fab_expanded);
    }

    private void rotateFab() {
        fab.animate().setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .rotation(fab_expanded ? 45f : 0f);
    }
}