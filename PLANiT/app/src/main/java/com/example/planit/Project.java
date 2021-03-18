package com.example.planit;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Project {
    final static int MAX_PROJECT_TITLE_LENGTH = 50;
    final static int MAX_PROJECT_TEXT_LENGTH = 500;
    private String title;
    private final UUID uuid;
    private Date dueDate;
    private final List<UUID> tagUUIDs = Collections.emptyList();
    private final List<UUID> taskUUIDs = Collections.emptyList();
    private String text = "";
    private final float completeness = .0f;

    public Project(String title) {
        if (title.length() == 0 || title.length() > MAX_PROJECT_TITLE_LENGTH)
            throw new IllegalArgumentException("Illegal title length");
        this.title = title;
        this.uuid = UUID.randomUUID();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title.length() == 0 || title.length() > MAX_PROJECT_TITLE_LENGTH)
            throw new IllegalArgumentException("Illegal title length");
        this.title = title;
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

    public boolean addTagUUID(UUID uuid) {
        return this.tagUUIDs.add(uuid);
    }

    public int searchTagUUID(UUID uuid) {
        return this.tagUUIDs.indexOf(uuid);
    }

    public boolean removeTagUUID(UUID uuid) {
        return this.tagUUIDs.remove(uuid);
    }

    public boolean addTaskUUID(UUID uuid) {
        return this.taskUUIDs.add(uuid);
    }

    public int searchTaskUUID(UUID uuid) {
        return this.taskUUIDs.indexOf(uuid);
    }

    public boolean removeTaskUUID(UUID uuid) {
        return this.taskUUIDs.remove(uuid);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (title.length() == 0 || title.length() > MAX_PROJECT_TEXT_LENGTH)
            throw new IllegalArgumentException("Illegal text length");
        this.text = text;
    }

    public float getCompleteness() {
        return completeness;
    }

    public void updateCompleteness() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
