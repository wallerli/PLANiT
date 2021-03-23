package com.example.planit;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProjectViewHolder extends RecyclerView.ViewHolder {

    TextView title, due, completeness;

    public ProjectViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.projectTitleTextView);
        due = itemView.findViewById(R.id.projectDueTextView);
//        completeness = itemView.findViewById(R.id.projectCompletenessTextView);
    }
}
