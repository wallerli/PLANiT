package com.example.planit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.UUID;

public class EditTaskActivity extends AppCompatActivity {

    public static String EDIT_TASK_ID = "com.example.planit.EDIT_TASK_ID";

    Task task;
    Size size;
    String title, text;
    Priority priority;
    Globals globals = Globals.getInstance();
    TextInputEditText titleEdit, textEdit;
    ChipGroup sizeChips;
    ChipGroup priorityChips;
    final Integer[] sizeChipIDs = new Integer[] {R.id.tiny_chip, R.id.small_chip, R.id.medium_chip, R.id.large_chip, R.id.huge_chip};
    final Integer[] priorityChipIDs = new Integer[] {R.id.low_chip, R.id.moderate_chip, R.id.high_chip, R.id.critical_chip};

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
            task = new Task("New Task");
            toolbar.setTitle("Add New Task");
        }

        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_toolbar_edit);
        toolbar.setNavigationOnClickListener(view -> finish());

        titleEdit = findViewById(R.id.task_title_text);
        textEdit = findViewById(R.id.edit_description);
        sizeChips = findViewById(R.id.size_chips);
        priorityChips = findViewById(R.id.priority_chips);

        titleEdit.setText(task.getTitle());
        textEdit.setText(task.getText());
        sizeChips.check(sizeChipIDs[task.getSize().ordinal()]);
        priorityChips.check(priorityChipIDs[task.getPriority().ordinal()]);

        sizeChips.setOnCheckedChangeListener((group, checkedId) ->
                size = Size.values()[Arrays.asList(sizeChipIDs).indexOf(checkedId)]);

        priorityChips.setOnCheckedChangeListener((group, checkedId) ->
                priority = Priority.values()[Arrays.asList(priorityChipIDs).indexOf(checkedId)]);

        titleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Add validation here
            }

            @Override
            public void afterTextChanged(Editable s) {
                title = s.toString();
            }
        });

        textEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Add validation here
            }

            @Override
            public void afterTextChanged(Editable s) {
                text = s.toString();
            }
        });
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