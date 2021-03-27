package com.example.planit;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.UUID;

import static com.example.planit.MainActivity.VIEW_TASK_ID;

public class TaskViewHolder extends RecyclerView.ViewHolder {

    TextView title, description;
    UUID taskUUID;
    CircularProgressIndicator indicator;
    boolean completed;
    boolean unblocked;
    final int completeThickness;
    final int incompleteThickness;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.taskTitleTextView);
        description = itemView.findViewById(R.id.taskDescriptionTextView);
        indicator = itemView.findViewById(R.id.task_indicator);

        itemView.findViewById(R.id.taskClickBox).setOnClickListener(v -> {
            openTask(v.getContext());
        });

        completeThickness = (int) itemView.getResources().getDimension(R.dimen.task_indicator_complete_thickness);
        incompleteThickness = (int) itemView.getResources().getDimension(R.dimen.task_indicator_incomplete_thickness);
    }

    public void setComplete() {
        indicator.setProgress(100);
        indicator.setTrackThickness(completeThickness);
    }

    public void setIncomplete() {
        indicator.setProgress(100);
        indicator.setTrackThickness(incompleteThickness);
    }

    public void setBlocked() {
        indicator.setProgress(0);
        indicator.setTrackThickness(incompleteThickness);
    }

    public void openTask(Context c) {
        Intent intent = new Intent(c, ViewTaskActivity.class);
        intent.putExtra(VIEW_TASK_ID, taskUUID.toString());
        c.startActivity(intent);
    }
}
