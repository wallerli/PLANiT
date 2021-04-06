package com.example.planit;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

enum Size { TINY, SMALL, MEDIUM, LARGE, HUGE }

enum Priority { LOW, MODERATE, HIGH, CRITICAL }

public class Task {
    private String title;
    private UUID uuid = UUID.randomUUID();
    private boolean complete = false;
    private Size size = Size.MEDIUM;
    private Priority priority = Priority.MODERATE;
    private Set<UUID> tags = new HashSet<>();
    private Set<UUID> blockers = new HashSet<>();
    private String text = "";

    public Task(String title) {
        this.setTitle(title);
    }

    public Task(UUID uuid, String title, boolean completed, Size size, Priority priority, String text) {
        this.uuid = uuid;
        this.setTitle(title);
        setCompleteStatus(completed);
        this.size = size;
        this.priority = priority;
        setText(text);
    }

    public Task(String title, Size size, Priority priority) {
        this.setTitle(title);
        this.size = size;
        this.priority = priority;
    }

    public Task(Task originalTask) {
        this.title = originalTask.title;
        this.uuid = originalTask.uuid;
        this.complete = originalTask.complete;
        this.size = originalTask.size;
        this.priority = originalTask.priority;
        this.tags = originalTask.tags;
        this.blockers = originalTask.blockers;
        this.text = originalTask.text;
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
        return (title.length() == 0) ? 1 : title.length() > Globals.MAX_TITLE_LENGTH ? 2 : 0;
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean getCompleteStatus() {
        return complete;
    }

    public void setCompleteStatus(boolean complete) {
        this.complete = complete;
    }

    public Size getSize() {
        return size;
    }

    public float getFloatSize() {
        return (size.ordinal() + 1f) / 5f;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void addTag(UUID uuid) {
        this.tags.add(uuid);
    }

    public boolean containsTag(UUID uuid) {
        return this.tags.contains(uuid);
    }

    public void removeTag(UUID uuid) {
        this.tags.remove(uuid);
    }

    public void addBlocker(UUID uuid) {
        this.blockers.add(uuid);
    }

    public boolean containsBlocker(UUID uuid) {
        return this.blockers.contains(uuid);
    }

    public void removeBlocker(UUID uuid) {
        this.blockers.remove(uuid);
    }

    public void removeAllBlockers() { this.blockers = new HashSet<>(); }

    public Set<UUID> getBlockers() {
        return blockers;
    }

    public int getBlockersSize() {
        return blockers.size();
    }

    public List<UUID> getTags() {
        return new ArrayList<>(tags);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<UUID> getOrderedBlockers() {
        Globals globals = Globals.getInstance();
        return blockers.stream().map(globals::getTask).sorted(new Globals.TaskComparator()).map(Task::getUUID).collect(Collectors.toList());
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
}
