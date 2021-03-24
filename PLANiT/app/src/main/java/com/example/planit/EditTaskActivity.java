package com.example.planit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class EditTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        Toolbar toolbar = findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_toolbar_edit);

        toolbar.setNavigationOnClickListener(view -> finish());
    }
}