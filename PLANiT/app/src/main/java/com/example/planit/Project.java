package com.example.planit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Project {
    private String title;
    private final UUID uuid = UUID.randomUUID();
    private Date dueDate;
    private final Set<UUID> tags = new HashSet<>();
    private final Set<UUID> tasks = new HashSet<>();
    private String text = "";
    private float completeness = .0f;

    public Project(String title) {
        this.setTitle(title);
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
    public int validateTitle(String title) {
        return (title.length() == 0) ? 1 : (title.length() > R.dimen.max_title_length) ? 2 : 0;
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

    /**
     * Ordered by number of blockers, largest first
     */
    public List<UUID> getOrderedTasks() {
        Globals globals = Globals.getInstance();
        List<UUID> sortedTasks = new ArrayList<>(tasks);
        Collections.sort(sortedTasks, (task1, task2) -> {
            // inverse for descending
            return globals.getTask(task2).getBlockers().size() - globals.getTask(task1).getBlockers().size();
        });
        return sortedTasks;
    }

    public boolean removeTask(UUID uuid) {
        return this.tasks.remove(uuid);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text.length() == 0 || text.length() > R.dimen.max_text_length)
            throw new IllegalArgumentException("Project text length not in range: " + 1 + "-" + R.dimen.max_text_length);
        this.text = text;
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
