package com.example.planit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.UUID;

public class ViewTaskActivity extends AppCompatActivity {

    Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        Intent intent = getIntent();
        UUID task_id = UUID.fromString(intent.getStringExtra(MainActivity.VIEW_TASK_ID));

        TextView textView = findViewById(R.id.text_task_id);
        textView.setText(task_id.toString());
    }
}