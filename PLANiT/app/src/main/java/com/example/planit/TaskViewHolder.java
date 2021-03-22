package com.example.planit;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskViewHolder extends RecyclerView.ViewHolder {

    TextView title, projectTitle, description;

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.taskTitleTextView);
        projectTitle = itemView.findViewById(R.id.taskProjectTitleTextView);
        description = itemView.findViewById(R.id.taskDescriptionTextView);
    }
}
