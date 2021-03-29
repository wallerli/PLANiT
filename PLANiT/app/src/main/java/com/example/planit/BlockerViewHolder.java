package com.example.planit;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.CheckBox;
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

public class BlockerViewHolder extends RecyclerView.ViewHolder {

    TextView title, description;
    UUID taskUUID;
    CircularProgressIndicator indicator;
    ChipGroup chips;
    Chip sizeChip, priorityChip;
    boolean completed;
    boolean unblocked;

    CheckBox checkBox;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public BlockerViewHolder(@NonNull View itemView) {
        super(itemView);

        checkBox = itemView.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Responds to checkbox being checked/unchecked
        });

//        title = itemView.findViewById(R.id.taskTitleTextView);
//        description = itemView.findViewById(R.id.taskDescriptionTextView);
//        indicator = itemView.findViewById(R.id.task_indicator);
//        chips = itemView.findViewById(R.id.taskTags);
//        sizeChip = itemView.findViewById(R.id.sizeChip);
//        priorityChip = itemView.findViewById(R.id.priorityChip);
//
//        itemView.findViewById(R.id.taskClickBox).setOnClickListener(v -> {
//            openTask(v.getContext());
//        });
//
//        completeThickness = (int) itemView.getResources().getDimension(R.dimen.task_indicator_complete_thickness);
//        incompleteThickness = (int) itemView.getResources().getDimension(R.dimen.task_indicator_incomplete_thickness);
    }
}
