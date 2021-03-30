package com.example.planit;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.UUID;

import static com.example.planit.MainActivity.VIEW_TASK_ID;

public class TaskViewHolder extends RecyclerView.ViewHolder {

    TextView title, description;
    UUID taskUUID;
    CircularProgressIndicator indicator;
    ChipGroup chips;
    Chip sizeChip, priorityChip;
    ImageView blockedIndicator;
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
        chips = itemView.findViewById(R.id.taskTags);
        sizeChip = itemView.findViewById(R.id.sizeChip);
        priorityChip = itemView.findViewById(R.id.priorityChip);
        blockedIndicator = itemView.findViewById(R.id.blocked_indicator);

        itemView.findViewById(R.id.taskClickBox).setOnClickListener(v -> {
            openTask(v.getContext());
        });

        completeThickness = (int) itemView.getResources().getDimension(R.dimen.task_indicator_complete_thickness);
        incompleteThickness = (int) itemView.getResources().getDimension(R.dimen.task_indicator_incomplete_thickness);
    }

    public void setComplete() {
        indicator.setProgress(100);
        indicator.setTrackThickness(completeThickness);
        blockedIndicator.setVisibility(View.INVISIBLE);
    }

    public void setIncomplete() {
        indicator.setProgress(100);
        indicator.setTrackThickness(incompleteThickness);
        blockedIndicator.setVisibility(View.INVISIBLE);
    }

    public void setBlocked() {
        indicator.setProgress(0);
        indicator.setTrackThickness(incompleteThickness);
        blockedIndicator.setVisibility(View.VISIBLE);
    }

    public void openTask(Context c) {
        Intent intent = new Intent(c, ViewTaskActivity.class);
        intent.putExtra(VIEW_TASK_ID, taskUUID.toString());
        c.startActivity(intent);
    }
}
