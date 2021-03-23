package com.example.planit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

public class TaskAdapter extends RecyclerView.Adapter<com.example.planit.TaskViewHolder> {

    private final Context mCtx;
    private final List<UUID> tasks;

    public TaskAdapter(Context mCtx, List<UUID> tasks) {
        this.mCtx = mCtx;
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public com.example.planit.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_tasks, null);
        return new com.example.planit.TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.planit.TaskViewHolder holder, int position) {
        Task task = Globals.getInstance().getTask(tasks.get(position));
        Project parentProject = Globals.getInstance().getParentProject(task.getUUID());
        holder.title.setText(task.getTitle());
        holder.description.setText(task.getText());
        holder.projectTitle.setText(parentProject.getTitle());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
