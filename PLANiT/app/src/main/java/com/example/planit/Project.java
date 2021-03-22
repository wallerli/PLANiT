package com.example.planit;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Project {
    private String title;
    private final UUID uuid = UUID.randomUUID();
    private Date dueDate;
    private final List<UUID> tagUUIDs = Collections.emptyList();
    private final List<UUID> taskUUIDs = Collections.emptyList();
    private String text = "";
    private final float completeness = .0f;

    public Project(String title) {
        this.setTitle(title);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title.length() == 0 || title.length() > R.dimen.max_title_length)
            throw new IllegalArgumentException("Project title length not in range: " + 1 + "-" + R.dimen.max_title_length);
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
        if (text.length() == 0 || text.length() > R.dimen.max_text_length)
            throw new IllegalArgumentException("Project text length not in range: " + 1 + "-" + R.dimen.max_text_length);
        this.text = text;
    }

    public float getCompleteness() {
        return completeness;
    }

    public void updateCompleteness() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
