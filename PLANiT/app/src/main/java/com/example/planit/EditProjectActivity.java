package com.example.planit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Build;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import static com.example.planit.MainActivity.VIEW_PROJECT_ID;

public class EditProjectActivity extends AppCompatActivity {

    public static String EDIT_PROJECT_ID = "com.example.planit.EDIT_PROJECT_ID";
    Globals globals = Globals.getInstance();

    Project project;
    TextInputEditText title, text;
    Button dueDate, dueTime;
    MaterialDatePicker<Long> datePicker;
    MaterialTimePicker timePicker;
    ChipGroup tagChips;
    TextView emptyTagsText;

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault());
    Date date = new Date(MaterialDatePicker.todayInUtcMilliseconds());
    String strDate;
    String strTime;

    boolean newProject = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initializing xml structure
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);
        Toolbar toolbar = findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_toolbar_edit);

        // Filling Content
        Intent intent = getIntent();

        title = findViewById(R.id.project_title_text);
        dueDate = findViewById(R.id.due_date_value);
        dueTime = findViewById(R.id.due_time_value);
        text = findViewById(R.id.edit_description);
        tagChips = findViewById(R.id.tag_chips);
        emptyTagsText = findViewById(R.id.empty_tags_text);

        if (intent.getStringExtra(EDIT_PROJECT_ID) != null) {
            project = new Project(globals.getProject(UUID.fromString(intent.getStringExtra(EDIT_PROJECT_ID))));
            toolbar.setTitle("Edit Project");
            text.setText(project.getText());
            if (project.getDueDate() != null) {
                date = project.getDueDate();
                strDate = dateFormat.format(date);
                strTime = timeFormat.format(date);
                dueDate.setText(strDate);
                dueTime.setText(strTime);
            }
        }
        else {
            project = new Project("New Project");
            toolbar.setTitle("Add New Project");
            newProject = true;
        }
        title.setText(project.getTitle());

        toolbar.setNavigationOnClickListener(view -> finish());
        dueDate.setOnClickListener(this::showDatePickerDialog);
        dueTime.setOnClickListener(this::showTimePickerDialog);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        project.getTags().forEach(t -> {
            Tag tag = globals.getTag(t);
            Chip lChip = new Chip(this);
            lChip.setText(tag.getName());
            if (tag.getHexColor() != -1) {
                lChip.setTextColor(getResources().getColor(R.color.white));
                lChip.setChipBackgroundColor(ColorStateList.valueOf(tag.getHexColor()));
            }
            lChip.setClickable(false);
            lChip.setFocusable(false);
            tagChips.addView(lChip);
        });

        if (project.getTags().size() == 0)
            emptyTagsText.setVisibility(View.VISIBLE);
        else
            emptyTagsText.setVisibility(View.INVISIBLE);
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
            globals.addProject(project);
            finish();
            if (newProject) {
                Intent intent = new Intent(this, ViewProjectActivity.class);
                intent.putExtra(VIEW_PROJECT_ID, project.getUUID().toString());
                startActivity(intent);
            }
        }

        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    public void showTimePickerDialog(View v) {
         timePicker = new MaterialTimePicker.Builder()
                        .setTitleText("Select time")
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(23)
                        .setMinute(59)
                        .build();
        timePicker.show(getSupportFragmentManager(), timePicker.toString());
        timePicker.addOnPositiveButtonClickListener(t -> {
            int hour = timePicker.getHour();
            String AMPM = "AM";
            if (hour >= 12) {
                hour -= 12;
                AMPM = "PM";
            }
            strTime = String.format(Locale.getDefault(), "%02d:%02d %s", hour, timePicker.getMinute(), AMPM);
            dueTime.setText(strTime);
            updateDate();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showDatePickerDialog(View v) {
        datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(date.getTime())
                        .build();
        datePicker.show(getSupportFragmentManager(), datePicker.toString());
        datePicker.addOnPositiveButtonClickListener(l -> {
            strDate = dateFormat.format(new Date(l));
            dueDate.setText(strDate);
            updateDate();
        });
    }

    private void updateDate() {
        try {
            if (strTime != null && !strTime.equals("null")) {
                date = dateTimeFormat.parse(strDate + " " + strTime);
            }
            else if (strDate != null && !strDate.equals("null")) {
                date = dateTimeFormat.parse(strDate + " 11:59 PM");
            }
        } catch (ParseException e) {
            System.out.println("Failed to parse: " + e);
        }
        project.setDueDate(date);
    }

    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }
}