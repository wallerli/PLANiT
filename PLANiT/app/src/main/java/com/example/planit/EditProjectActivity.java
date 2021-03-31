package com.example.planit;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Build;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import static android.view.View.GONE;
import static com.example.planit.MainActivity.VIEW_PROJECT_ID;

public class EditProjectActivity extends AppCompatActivity {

    public static String EDIT_PROJECT_ID = "com.example.planit.EDIT_PROJECT_ID";
    Globals globals = Globals.getInstance();

    Project project;
    TextInputEditText title, text;
    TextInputLayout titleInput, textInput;
    Button dueDate, dueTime, dueCLear, delete;
    MaterialDatePicker<Long> datePicker;
    MaterialTimePicker timePicker;
    ChipGroup tagChips;
    TextView emptyTagsText;

    SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy", Locale.getDefault());
    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("M/d/yyyy h:mm a", Locale.getDefault());
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
        toolbar.inflateMenu(R.menu.menu_toolbar_edit);

        // Filling Content
        Intent intent = getIntent();

        title = findViewById(R.id.project_title_text);
        dueDate = findViewById(R.id.due_date_value);
        dueTime = findViewById(R.id.due_time_value);
        text = findViewById(R.id.edit_description);
        tagChips = findViewById(R.id.tag_chips);
        emptyTagsText = findViewById(R.id.empty_tags_text);
        dueCLear = findViewById(R.id.clear_due);
        delete = findViewById(R.id.delete_button);
        titleInput = findViewById(R.id.edit_project_title);
        textInput = findViewById(R.id.descriptionText);
        titleInput.setCounterMaxLength(Globals.MAX_TITLE_LENGTH);
        textInput.setCounterMaxLength(Globals.MAX_TEXT_LENGTH);

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
            title.setText(project.getTitle());
        }
        else {
            project = new Project("");
            toolbar.setTitle("Add New Project");
            delete.setVisibility(GONE);
            newProject = true;
            title.requestFocus();
        }
        setSupportActionBar(toolbar);
        Globals.updateToolbarColor(this, toolbar);
        dueCLear.setEnabled(project.getDueDate() != null);

        toolbar.setNavigationOnClickListener(view -> finish());
        dueCLear.setOnClickListener(this::clearDue);
        dueDate.setOnClickListener(this::showDatePickerDialog);
        dueTime.setOnClickListener(this::showTimePickerDialog);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        updateChips();

        // Listeners
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch (Project.validateTitle(s.toString())) {
                    case 1:
                        titleInput.setError("The title cannot be empty.");
                        break;
                    case 2:
                        titleInput.setError("This title is too long.");
                        break;
                    default:
                        titleInput.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                project.setTitle(s.toString());
            }
        });

        title.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                switch (Task.validateTitle(project.getTitle())) {
                    case 1:
                        titleInput.setError("The title cannot be empty.");
                        break;
                    case 2:
                        titleInput.setError("This title is too long.");
                        break;
                    default:
                        titleInput.setError(null);
                }
            }
        });

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch (Project.validateText(s.toString())) {
                    case 2:
                        textInput.setError("This description is too long.");
                        break;
                    default:
                        textInput.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                project.setText(s.toString());
            }
        });

        text.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                switch (Task.validateText(project.getText())) {
                    case 2:
                        textInput.setError("This description is too long.");
                        break;
                    default:
                        textInput.setError(null);
                }
            }
        });

        delete.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Delete \"" + project.getTitle() + "\"?");
            alertDialog.setMessage("Are you sure you want to delete this project? Deleting this project will also delete all of the tasks underneath it.\n\n This action cannot be undone!");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE",
                    (dialog, which) -> {
                        globals.removeProject(project.getUUID());
                        dialog.dismiss();
                        finish();
                        Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                    });
            alertDialog.show();
            Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (b != null) {
                TypedValue typedValue = new TypedValue();
                getTheme().resolveAttribute(R.attr.colorError, typedValue, true);
                @ColorInt int color = typedValue.data;
                b.setTextColor(color);
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
            int v1 = Project.validateTitle(project.getTitle());
            int v2 = Project.validateText(project.getText());
            if (v1 != 0 || v2 != 0) {
                int problemCode = -1;
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Please complete all required fields");
                String message = "";
                if (v1 == 1) {
                    message += "* The title cannot be empty.\n";
                    problemCode = 1;
                }
                if (v1 == 2) {
                    message += "* This title is too long. Make sure your title is under " + Globals.MAX_TITLE_LENGTH + " characters.\n";
                    problemCode = 1;
                }
                if (v2 == 2) {
                    message += "* The description is too long. Make sure your description is under " + Globals.MAX_TEXT_LENGTH + " characters.\n";
                    problemCode = 2;
                }
                alertDialog.setMessage(message);
                int finalProblemCode = problemCode;
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DISMISS",
                        (dialog, which) -> {
                            dialog.dismiss();
                            switch (finalProblemCode) {
                                case 1:
                                    title.requestFocus();
                                    break;
                                case 2:
                                    text.requestFocus();
                                    break;
                            }
                        });
                alertDialog.show();
            } else {
                globals.addProject(project);
                finish();
                if (newProject) {
                    Intent intent = new Intent(this, ViewProjectActivity.class);
                    intent.putExtra(VIEW_PROJECT_ID, project.getUUID().toString());
                    startActivity(intent);
                }
                Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
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
            strTime = String.format(Locale.getDefault(), "%d:%02d %s", hour, timePicker.getMinute(), AMPM);
            dueTime.setText(strTime);
            updateDate();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void clearDue(View v) {
        date = new Date(MaterialDatePicker.todayInUtcMilliseconds());
        strDate = null;
        strTime = null;
        dueDate.setText(R.string.no_due_date_selected_text);
        dueTime.setText(R.string.no_due_time_selected_text);
        updateDate();
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
        if (strDate == null && strTime == null) {
            project.setDueDate(null);
        } else {
            try {
                if (strTime != null && strDate != null) {
                    date = dateTimeFormat.parse(strDate + " " + strTime);
                } else if (strDate != null) {
                    date = dateTimeFormat.parse(strDate + " 11:59 PM");
                } else {
                    date = dateTimeFormat.parse(dateFormat.format(date) + " " + strTime);
                }
            } catch (ParseException e) {
                System.out.println("Failed to parse: " + e);
            }
            project.setDueDate(date);
        }
        dueCLear.setEnabled(strTime != null || strDate != null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateChips() {
        tagChips.removeAllViews();
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
            lChip.setCloseIconVisible(true);
            lChip.setOnCloseIconClickListener(v ->
                    Toast.makeText(getApplicationContext(),R.string.to_be_implemented,Toast.LENGTH_SHORT).show());
            tagChips.addView(lChip);
        });
        Chip lChip = new Chip(this);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;
        lChip.setTextColor(color);
        lChip.setText(R.string.create_tag);
        lChip.setOnClickListener(v ->
                Toast.makeText(getApplicationContext(),R.string.to_be_implemented,Toast.LENGTH_SHORT).show());
        tagChips.addView(lChip);
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