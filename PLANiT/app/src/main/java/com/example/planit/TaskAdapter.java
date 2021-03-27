package com.example.planit;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
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

        holder.indicator.setOnClickListener(v -> {
            int ret;
            if (holder.completed) {
                ret = Globals.getInstance().setTaskCompleted(holder.taskUUID, false);
                if (ret == 0) {
                    holder.completed = false;
                    holder.setIncomplete();
                } else if (ret == 3) {
                    holder.completed = false;
                    holder.unblocked = false;
                    holder.setBlocked();
                }
            } else {
                ret = Globals.getInstance().setTaskCompleted(holder.taskUUID, true);
                if (ret == 0) {
                    holder.completed = true;
                    holder.setComplete();
                } else if (ret == 2) {
                    holder.completed = false;
                    holder.unblocked = false;
                    holder.setBlocked();
                    holder.completed = false;
                    holder.unblocked = false;
                    holder.setBlocked();
                    AlertDialog alertDialog = new AlertDialog.Builder(mCtx).create();
                    alertDialog.setTitle("The Task is Blocked");
                    alertDialog.setMessage("Please complete all its blockers before marking the task completed.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DISMISS",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "VIEW",
                            (dialog, which) -> {
                        dialog.dismiss();
                        holder.openTask(mCtx);
                    });
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
