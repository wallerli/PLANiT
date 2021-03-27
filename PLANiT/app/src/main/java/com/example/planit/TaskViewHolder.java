package com.example.planit;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
            Context context = v.getContext();
            Intent intent = new Intent(context, ViewTaskActivity.class);
            intent.putExtra(VIEW_TASK_ID, taskUUID.toString());
            context.startActivity(intent);
        });

        completeThickness = (int) itemView.getResources().getDimension(R.dimen.task_indicator_complete_thickness);
        incompleteThickness = (int) itemView.getResources().getDimension(R.dimen.task_indicator_incomplete_thickness);

        indicator.setOnClickListener(v -> {
            int ret;
            if (completed) {
                ret = Globals.getInstance().setTaskCompleted(taskUUID, false);
                if (ret == 0) {
                    completed = false;
                    setIncomplete();
                } else if (ret == 3) {
                    completed = false;
                    unblocked = false;
                    setBlocked();
                }
            } else {
                ret = Globals.getInstance().setTaskCompleted(taskUUID, true);
                if (ret == 0) {
                    completed = true;
                    setComplete();
                } else if (ret == 2) {
                    completed = false;
                    unblocked = false;
                    setBlocked();
                }
            }
        });
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
}
