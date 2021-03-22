package com.example.planit;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

enum Size { TINY, SMALL, MEDIUM, LARGE, HUGE }

enum Priority { LOW, MODERATE, HIGH, CRITICAL }

public class Task {
    private String title;
    private final UUID uuid = UUID.randomUUID();
    private boolean complete = false;
    private Size size;
    private Priority priority;
    private final List<UUID> tagUUIDs = Collections.emptyList();
    private final List<UUID> blockerUUIDs = Collections.emptyList();
    private String text = "";

    public Task(String title, Size size, Priority priority) {
        this.setTitle(title);
        this.size = size;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title.length() == 0 || title.length() > R.dimen.max_title_length)
            throw new IllegalArgumentException("Task title length not in range: " + 1 + "-" + R.dimen.max_title_length);
        this.title = title;
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

    public void setSize(Size size) {
        this.size = size;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean addTagUUID(UUID uuid) {
        return this.tagUUIDs.add(uuid);
    }

    public int searchTagUUID(UUID uuid) {
        return this.tagUUIDs.indexOf(uuid);
    }

    public boolean removeTagUUID(UUID uuid) {
        return this.tagUUIDs.remove(uuid);
    }

    public boolean addBlockerUUID(UUID uuid) {
        return this.blockerUUIDs.add(uuid);
    }

    public int searchBlockerUUID(UUID uuid) {
        return this.blockerUUIDs.indexOf(uuid);
    }

    public boolean removeBlockerUUID(UUID uuid) {
        return this.blockerUUIDs.remove(uuid);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text.length() == 0 || text.length() > R.dimen.max_text_length)
            throw new IllegalArgumentException("Task text length not in range: " + 1 + "-" + R.dimen.max_text_length);
        this.text = text;
    }
}
