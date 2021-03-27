package com.example.planit;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProjectAdapter extends RecyclerView.Adapter<com.example.planit.ProjectViewHolder> {

    private final Context mCtx;
    private final List<UUID> projects;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ProjectAdapter(Context mCtx, List<UUID> projects) {
        this.mCtx = mCtx;
        this.projects = projects.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_projects, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = Globals.getInstance().getProject(projects.get(position));
        holder.title.setText(project.getTitle());
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault());
        Date due = project.getDueDate();
        if (due != null) {
            holder.due.setText(df.format(project.getDueDate()));
            if (due.getTime() < System.currentTimeMillis() && project.getCompleteness() < 1) {
                holder.due.setTextColor(mCtx.getResources().getColor(R.color.orange_700));
                holder.due.setTypeface(null, Typeface.BOLD);
            }
        } else {
            holder.due.setText(R.string.no_due_date);
        }
        holder.completeness_text.setText(String.format(Locale.getDefault(), "%.2f %%", 100 * project.getCompleteness()));
        holder.projectUUID = project.getUUID();
        holder.completeness = (int) (100 * project.getCompleteness());
        holder.updateProgress();
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }
}
