package com.example.planit;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.UUID;

import static com.example.planit.MainActivity.VIEW_TASK_ID;

public class TaskViewHolder extends RecyclerView.ViewHolder {

    TextView title, description;
    UUID taskUUID;

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.taskTitleTextView);
        description = itemView.findViewById(R.id.taskDescriptionTextView);

        itemView.findViewById(R.id.taskClickBox).setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ViewTaskActivity.class);
            intent.putExtra(VIEW_TASK_ID, taskUUID.toString());
            context.startActivity(intent);
        });
    }
}
