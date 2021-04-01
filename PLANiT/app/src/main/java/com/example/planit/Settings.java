package com.example.planit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_container);

        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        //setSupportActionBar(toolbar);
        Globals.updateToolbarColor(this, toolbar);
        toolbar.inflateMenu(R.menu.menu_toolbar_settings);
        toolbar.setNavigationOnClickListener(view -> finish());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar_settings, menu);
        return true;
    }


}