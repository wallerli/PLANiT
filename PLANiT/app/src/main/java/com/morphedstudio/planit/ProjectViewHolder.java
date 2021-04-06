package com.morphedstudio.planit;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.UUID;

import static com.morphedstudio.planit.MainActivity.VIEW_PROJECT_ID;

public class ProjectViewHolder extends RecyclerView.ViewHolder {

    TextView title, description, due, completeness_text;
    UUID projectUUID;
    int completeness;
    CircularProgressIndicator indicator;

    public ProjectViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.projectTitleTextView);
        description = itemView.findViewById(R.id.projectDescriptionTextView);
        due = itemView.findViewById(R.id.projectDueTextView);
        completeness_text = itemView.findViewById(R.id.projectCompletenessTextView);
        indicator = itemView.findViewById(R.id.project_indicator);
        itemView.findViewById(R.id.projectClickBox).setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ViewProjectActivity.class);
            intent.putExtra(VIEW_PROJECT_ID, projectUUID.toString());
            context.startActivity(intent);
        });
    }

    public void updateProgress() {
        indicator.setProgress(completeness);
    }
}
