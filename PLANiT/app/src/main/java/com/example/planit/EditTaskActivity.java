package com.example.planit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

public class EditTaskActivity extends AppCompatActivity {

    public static String EDIT_TASK_ID = "com.example.planit.EDIT_TASK_ID";

    Task task;
    Size size;
    Globals globals = Globals.getInstance();
    TextInputEditText title, text;
    ChipGroup sizeChips;
    ChipGroup priorityChips;
    final int[] sizeChipIDs = new int[] {R.id.tiny_chip, R.id.small_chip, R.id.medium_chip, R.id.large_chip, R.id.huge_chip};
    final int[] priorityChipIDs = new int[] {R.id.low_chip, R.id.moderate_chip, R.id.high_chip, R.id.critical_chip};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        Toolbar toolbar = findViewById(R.id.edit_toolbar);
        Intent intent = getIntent();
        if (intent.getStringExtra(EDIT_TASK_ID) != null) {
            task = globals.getTask(UUID.fromString(intent.getStringExtra(EDIT_TASK_ID)));
            toolbar.setTitle("Edit Task");
        }
        else {
            task = globals.getTask();
            toolbar.setTitle("Add New Task");
        }
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_toolbar_edit);
        toolbar.setNavigationOnClickListener(view -> finish());

        title = findViewById(R.id.task_title_text);
        text = findViewById(R.id.edit_description);
        sizeChips = findViewById(R.id.size_chips);
        priorityChips = findViewById(R.id.priority_chips);

        title.setText(task.getTitle());
        text.setText(task.getText());
        sizeChips.check(sizeChipIDs[task.getSize().ordinal()]);
        priorityChips.check(priorityChipIDs[task.getPriority().ordinal()]);
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