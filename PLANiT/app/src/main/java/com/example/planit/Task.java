package com.example.planit;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

enum Size { TINY, SMALL, MEDIUM, LARGE, HUGE }

enum Priority { LOW, MODERATE, HIGH, CRITICAL }

public class Task {
    private String title;
    private final UUID uuid = UUID.randomUUID();
    private boolean complete = false;
    private Size size;
    private Priority priority;
    private final Set<UUID> tags = Collections.emptySet();
    private final Set<UUID> blockers = Collections.emptySet();
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

    public boolean addTag(UUID uuid) {
        return this.tags.add(uuid);
    }

    public boolean containsTag(UUID uuid) {
        return this.tags.contains(uuid);
    }

    public boolean removeTag(UUID uuid) {
        return this.tags.remove(uuid);
    }

    public boolean addBlocker(UUID uuid) {
        return this.blockers.add(uuid);
    }

    public boolean containsBlocker(UUID uuid) {
        return this.blockers.contains(uuid);
    }

    public boolean removeBlocker(UUID uuid) {
        return this.blockers.remove(uuid);
    }

    /**
     * @param uuid the uuid of new blocker
     * @return true if the new blocker is not blocked by current task
     */
    public boolean verifyBlocker(UUID uuid) {
        Globals globals = Globals.getInstance();
        return globals.getTask(uuid).allBlockers().contains(this.uuid);
    }

    /**
     * @return recursively all blockers of the current task
     */
    public Set<UUID> allBlockers() {
        Set<UUID> allBlockers = Collections.emptySet();
        Globals globals = Globals.getInstance();
        for (UUID blocker : this.blockers) {
            allBlockers.add(blocker);
            allBlockers.addAll(globals.getTask(blocker).allBlockers());
        }
        return allBlockers;
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
