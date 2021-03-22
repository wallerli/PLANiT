package com.example.planit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Globals{

    private static Globals globals_instance = null;
    private final Map<UUID, Project> projects = new HashMap<UUID, Project>();
    private final Map<UUID, Tag> tags = new HashMap<UUID, Tag>();
    private final Map<UUID, Task> tasks = new HashMap<UUID, Task>();

    private Globals() {
        projects.put(null, new Project("New Project"));
        tags.put(null, new Tag("New Tag"));
        tasks.put(null, new Task("New Task", Size.MEDIUM, Priority.MODERATE));
    }

    public static Globals getInstance() {
        if (globals_instance == null)
            globals_instance = new Globals();
        return globals_instance;
    }

    public Project addProject(Project project) {
        return projects.put(project.getUUID(), project);
    }

    public Tag addTag(Tag tag) {
        return tags.put(tag.getUUID(), tag);
    }

    public Task addTask(Task task) {
        return tasks.put(task.getUUID(), task);
    }

    public Project getProject(UUID projectUUID) {
        return projects.get(projectUUID);
    }

    public Tag getTag(UUID tagUUID) {
        return tags.remove(tagUUID);
    }

    public Task getTask(UUID taskUUID) {
        return tasks.get(taskUUID);
    }

    public Project removeProject(UUID projectUUID) {
        for (UUID taskUUID : this.getProject(projectUUID).getTasks()) {
            this.removeTask(taskUUID);
        }
        return projects.remove(projectUUID);
    }

    public Tag removeTag(UUID tagUUID) {
        for (Project project : projects.values()) {
            project.removeTag(tagUUID);
        }
        for (Task task : tasks.values()) {
            task.removeTag(tagUUID);
        }
        return tags.remove(tagUUID);
    }

    public Task removeTask(UUID taskUUID) {
        // This for loop is needed if we will allow blockers between projects.
        for (Task task : tasks.values()) {
            task.removeBlocker(taskUUID);
        }
        return tasks.remove(taskUUID);
    }
}