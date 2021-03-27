package com.example.planit;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaskAdapter extends RecyclerView.Adapter<com.example.planit.TaskViewHolder> {

    private final Context mCtx;
    private final List<UUID> tasks;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public TaskAdapter(Context mCtx, List<UUID> tasks) {
        this.mCtx = mCtx;
        this.tasks = tasks.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_tasks, parent, false);
        return new TaskViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Globals globals = Globals.getInstance();
        Task task = globals.getTask(tasks.get(position));
        holder.title.setText(task.getTitle());
        holder.description.setText(task.getText());
        holder.taskUUID = task.getUUID();
        holder.completed = task.getCompleteStatus();
        holder.unblocked = task.getBlockers().stream().allMatch(b -> globals.getTask(b).getCompleteStatus());
        if (holder.completed) {
            holder.setComplete();
        } else if (holder.unblocked) {
            holder.setIncomplete();
        } else {
            holder.setBlocked();
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
