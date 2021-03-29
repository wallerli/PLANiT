package com.example.planit;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

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
        String text = task.getText();
        if (text.length() == 0) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.description.getLayoutParams();
            params.setMargins(params.leftMargin, 0, params.rightMargin, params.bottomMargin);
            params.height = 0;
            holder.description.setLayoutParams(params);
        }
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

        String taskSize = task.getSize().toString();
        holder.sizeChip.setText(String.format("%s%s", taskSize.charAt(0), taskSize.substring(1).toLowerCase()));
        holder.sizeChip.setChecked(true);
        String taskPriority = task.getPriority().toString();
        holder.priorityChip.setText(String.format("%s%s", taskPriority.charAt(0), taskPriority.substring(1).toLowerCase()));
        holder.priorityChip.setChecked(true);
        holder.chips.removeAllViews();
        List<UUID> tags = task.getTags();
        if (tags.size() == 0)
            holder.chips.setVisibility(View.GONE);
        else {
            holder.chips.setVisibility(View.VISIBLE);
            task.getTags().forEach(t -> {
                Tag tag = globals.getTag(t);
                Chip lChip = new Chip(mCtx);
                lChip.setText(tag.getName());
                if (tag.getHexColor() != -1) {
                    lChip.setTextColor(mCtx.getResources().getColor(R.color.white));
                    lChip.setChipBackgroundColor(ColorStateList.valueOf(tag.getHexColor()));
                }
                lChip.setEnsureMinTouchTargetSize(false);
                lChip.setClickable(false);
                holder.chips.addView(lChip);
            });
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
            if (mCtx instanceof ViewProjectActivity) {
                ((ViewProjectActivity) mCtx).updateProgress();
            }
            else if (mCtx instanceof ViewTaskActivity) {
                ((ViewTaskActivity) mCtx).updateCompleteness();
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
