package com.example.planit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ProjectAdapter extends RecyclerView.Adapter<com.example.planit.ProjectViewHolder> {

    private final Context mCtx;
    private final List<UUID> projects;

    public ProjectAdapter(Context mCtx, List<UUID> projects) {
        this.mCtx = mCtx;
        this.projects = projects;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_projects, null);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = Globals.getInstance().getProject(projects.get(position));
        holder.title.setText(project.getTitle());
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        holder.due.setText(df.format(project.getDueDate()));
        holder.completeness.setText(String.format(Locale.getDefault(), "%.2f %%", 100 * project.getCompleteness()));
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }
}
