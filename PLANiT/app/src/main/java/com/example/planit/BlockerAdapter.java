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

public class BlockerAdapter extends RecyclerView.Adapter<com.example.planit.BlockerViewHolder> {

    Globals globals = Globals.getInstance();
    private final Context mCtx;
    private final List<UUID> tasks;
    private final Task contextTask;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public BlockerAdapter(Context mCtx, List<UUID> tasks, Task contextTask) {
        this.mCtx = mCtx;
        this.tasks = tasks.stream().filter(Objects::nonNull).collect(Collectors.toList());
        this.contextTask = contextTask;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public BlockerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_blockers, parent, false);
        return new BlockerViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull BlockerViewHolder holder, int position) {
        Task task = globals.getTask(tasks.get(position));
        holder.checkBox.setText(task.getTitle());
        if (contextTask.getBlockers().contains(task.getUUID())) {
            holder.checkBox.setChecked(true);
        }
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                contextTask.addBlocker(task.getUUID());
            }
            else {
                contextTask.removeBlocker(task.getUUID());
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
