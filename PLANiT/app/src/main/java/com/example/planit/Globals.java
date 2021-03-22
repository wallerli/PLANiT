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

    public Project deleteProject(UUID projectUuid) {
        return projects.remove(projectUuid);
    }

    public Tag deleteTag(UUID tagUuid) {
        return tags.remove(tagUuid);
    }

    public Task deleteTask(UUID taskUuid) {
        return tasks.remove(taskUuid);
    }

    public Project getProject(UUID projectUuid) {
        return projects.get(projectUuid);
    }

    public Tag getTag(UUID tagUuid) {
        return tags.remove(tagUuid);
    }

    public Task getTask(UUID taskUuid) {
        return tasks.get(taskUuid);
    }
}