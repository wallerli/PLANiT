package com.example.planit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static String VIEW_PROJECT_ID = "com.example.planit.VIEW_PROJECT_ID";
    public static String VIEW_TASK_ID = "com.example.planit.VIEW_TASK_ID";
    FloatingActionButton fab;
    boolean fab_expanded = false;
    FragmentPagerAdapter FragmentPagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    TextView logo;

    @Override
    public void onPause() {
        super.onPause();
        new Handler().postDelayed(() -> Globals.getInstance().save(this), 1000);
    }

    @Override
    public void onStop() {
        super.onStop();
        Globals.getInstance().save(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Globals.getInstance(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        logo = findViewById(R.id.app_logo);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Globals.updateToolbarColor(this, toolbar);

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(view -> {
            fab_expanded = !fab_expanded;
            rotateFab();
            fab.setExpanded(fab_expanded);
        });

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getApplication());
        logo.setOnLongClickListener(v -> {
            if (shared.getBoolean("demoMode", false)) {
                shared.edit().putBoolean("developer", true).apply();
                Toast.makeText(getApplicationContext(), R.string.turn_off_demo_mode_text,Toast.LENGTH_SHORT).show();
            } else {
                shared.edit().putBoolean("developer", !shared.getBoolean("developer", false)).apply();
                Toast.makeText(getApplicationContext(), shared.getBoolean("developer", false) ?
                        R.string.developer_on : R.string.developer_off ,Toast.LENGTH_SHORT).show();
            }
            return false;
        });

        setPagerAdapter();
        setTabLayout();
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setPagerAdapter() {
        FragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(FragmentPagerAdapter);
    }

    private void setTabLayout() {
        tabLayout.setupWithViewPager(viewPager);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("Projects");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText("Tasks");
    }

    public void openEditNewProject(View view){
        Intent intent = new Intent(this, EditProjectActivity.class);
        startActivity(intent);
        fab_expanded = false;
        rotateFab();
        fab.setExpanded(fab_expanded);
    }

    public void openEditNewTask(View view){
        Intent intent = new Intent(this, EditTaskActivity.class);
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

    public void openSetting(View view){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        //fab_expanded = false;
        //rotateFab();
        //fab.setExpanded(fab_expanded);
    }
}