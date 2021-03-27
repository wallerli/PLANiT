package com.example.planit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

enum Size { TINY, SMALL, MEDIUM, LARGE, HUGE }

enum Priority { LOW, MODERATE, HIGH, CRITICAL }

public class Task {
    private String title;
    private UUID uuid = UUID.randomUUID();
    private boolean complete = false;
    private Size size;
    private Priority priority;
    private Set<UUID> tags = new HashSet<>();
    private Set<UUID> blockers = new HashSet<>();
    private String text = "";

    public Task(String title) {
        this.setTitle(title);
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
    public int validateTitle(String title) {
        return (title.length() == 0) ? 1 : (title.length() > R.dimen.max_title_length) ? 2 : 0;
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
     * @return 0 if new blocker is legitimate;
     *          1 if new blocker is the same as current task;
     *          2 if new blocker is blocked by current task
     */
    public int verifyBlocker(UUID uuid) {
        Globals globals = Globals.getInstance();
        if (uuid.equals(this.uuid))
            return 1;
        if (globals.getTask(uuid).allBlockers().contains(this.uuid))
            return 2;
        return 0;
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

    public Set<UUID> getBlockers() {
        return blockers;
    }

    public List<UUID> getTags() {
        return new ArrayList<>(tags);
    }

    /**
     * Ordered by number of blockers, largest first
     */
    public List<UUID> getOrderedBlockers() {
        Globals globals = Globals.getInstance();
        List<UUID> sortedBlockers = new ArrayList<>(blockers);
        Collections.sort(sortedBlockers, (blocker1, blocker2) -> {
            int b1 = globals.getTask(blocker1).getBlockers().size();
            int b2 = globals.getTask(blocker2).getBlockers().size();
            // inverse for descending
            return globals.getTask(blocker2).getBlockers().size() - globals.getTask(blocker1).getBlockers().size();
        });
        return sortedBlockers;
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
