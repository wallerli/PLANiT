package com.example.planit;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Project {
    private String title;
    private UUID uuid = UUID.randomUUID();
    private Date dueDate;
    private Set<UUID> tags = new HashSet<>();
    private Set<UUID> tasks = new HashSet<>();
    private String text = "";
    private float completeness = .0f;

    public Project(String title) {
        this.setTitle(title);
    }

    public Project(Project originalProject) {
        this.title = originalProject.title;
        this.uuid = originalProject.uuid;
        this.dueDate = originalProject.dueDate;
        this.tags = originalProject.tags;
        this.tasks = originalProject.tasks;
        this.text = originalProject.text;
        this.completeness = originalProject.completeness;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return 0 for correct title; 1 for empty title; 2 for exceeding max length
     */
    public static int validateTitle(String title) {
        return title.length() == 0 ? 1 : title.length() > Globals.MAX_TITLE_LENGTH ? 2 : 0;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean addTag(UUID uuid) {
        return this.tags.add(uuid);
    }

    public boolean containsTag(UUID uuid) {
        return this.tags.contains(uuid);
    }

    public boolean removeTag(UUID uuid) {
        return this.tags.remove(uuid);
    }

    public boolean addTask(UUID uuid) {
        return this.tasks.add(uuid);
    }

    public boolean containsTask(UUID uuid) {
        return this.tasks.contains(uuid);
    }

    public Set<UUID> getTasks() {
        return this.tasks;
    }

    public List<UUID> getTags() {
        return new ArrayList<>(tags);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<UUID> getOrderedTasks() {
        Globals globals = Globals.getInstance();
        return tasks.stream().map(globals::getTask).sorted(new Globals.TaskComparator()).map(Task::getUUID).collect(Collectors.toList());
    }

    public boolean removeTask(UUID uuid) {
        return this.tasks.remove(uuid);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return 0 for correct text; 2 for exceeding max length
     */
    public static int validateText(String text) {
        return text.length() > Globals.MAX_TEXT_LENGTH ? 2 : 0;
    }

    public float getCompleteness() {
        updateCompleteness();
        return completeness;
    }

    public void updateCompleteness() {
        Globals globals = Globals.getInstance();
        float totalPoints = 0;
        float completedPoints = 0;
        if (tasks.size() == 0) {
            completeness = 0;
            return;
        }
        for(UUID taskUUID : tasks) {
            Task task = globals.getTask(taskUUID);
            if (task == null) continue;
            float pts = task.getFloatSize();
            totalPoints += pts;
            completedPoints += (task.getCompleteStatus() ? pts : 0);
        }
        completeness = completedPoints / totalPoints;
    }
}
