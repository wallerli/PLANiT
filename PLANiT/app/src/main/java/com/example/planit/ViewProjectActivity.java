package com.example.planit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;

public class ViewProjectActivity extends AppCompatActivity {

    Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_project);
        Toolbar toolbar = findViewById(R.id.view_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        UUID project_id = UUID.fromString(intent.getStringExtra(MainActivity.VIEW_PROJECT_ID));
    }
}
