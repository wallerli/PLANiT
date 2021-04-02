package com.example.planit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.TypedValue;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.sql.Timestamp;

import static android.content.Context.MODE_PRIVATE;

public class Globals {

    private static Globals globals_instance = null;
    private final Map<UUID, Project> projects = new HashMap<>();
    private final Map<UUID, Tag> tags = new HashMap<>();
    private final Map<UUID, Task> tasks = new HashMap<>();
    private final static String FILE_NAME = "storage.json";
    private final static String FILE_NAME_TEMP = "storage.json_temp";
    private final static String FILE_NAME_BACKUP = "storage.json_backup";
    private boolean demoMode = false;
    private boolean restored = false;

    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_TEXT_LENGTH = 500;
    public static final int MAX_TAG_LENGTH = 20;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Globals(Context ctx) {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
        demoMode = shared.getBoolean("demoMode", false);
        read(ctx);
        if (restored) {
            updateRestoredDueDate();
            save(ctx);
        }
    }

    public static Globals getInstance() {
        return globals_instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Globals getInstance(Context ctx) {
        if (globals_instance == null)
            globals_instance = new Globals(ctx);
        return globals_instance;
    }

    public int read(Context ctx) {
        if (!restored) {
            try {
                InputStream fis;
                if (demoMode) {
                    fis = ctx.getAssets().open(FILE_NAME_BACKUP);
                    restored = true;
                } else if (isStorageFilePresent(ctx)) {
                    fis = ctx.openFileInput(FILE_NAME);
                    restored = true;
                } else {
                    restored = false;
                    return 0;
                }
                InputStreamReader isr = new InputStreamReader(fis);
                JsonReader reader = new JsonReader(isr);
                tags.clear();
                tasks.clear();
                projects.clear();
                reader.beginObject();
                reader.nextName();
                reader.beginArray();
                while (reader.peek() != JsonToken.END_ARRAY) {
                    reader.beginObject();
                    reader.nextName();
                    UUID uuid = UUID.fromString(reader.nextString());
                    reader.nextName();
                    String name = reader.nextString();
                    reader.nextName();
                    int hex = reader.nextInt();
                    Tag tag = new Tag(uuid, name, hex);
                    tags.put(tag.getUUID(), tag);
                    reader.endObject();
                }
                reader.endArray();
                reader.nextName();
                reader.beginArray();
                while (reader.peek() != JsonToken.END_ARRAY) {
                    reader.beginObject();
                    reader.nextName();
                    UUID uuid = UUID.fromString(reader.nextString());
                    reader.nextName();
                    String title = reader.nextString();
                    reader.nextName();
                    boolean completed = reader.nextBoolean();
                    reader.nextName();
                    Size size = Size.valueOf(reader.nextString());
                    reader.nextName();
                    Priority priority = Priority.valueOf(reader.nextString());
                    reader.nextName();
                    String text = reader.nextString();
                    Task task = new Task(uuid, title, completed, size, priority, text);
                    reader.nextName();
                    reader.beginArray();
                    while (reader.peek() != JsonToken.END_ARRAY)
                        task.addTag(UUID.fromString(reader.nextString()));
                    reader.endArray();
                    reader.nextName();
                    reader.beginArray();
                    while (reader.peek() != JsonToken.END_ARRAY)
                        task.addBlocker(UUID.fromString(reader.nextString()));
                    reader.endArray();
                    tasks.put(task.getUUID(), task);
                    reader.endObject();
                }
                reader.endArray();
                reader.nextName();
                reader.beginArray();
                while (reader.peek() != JsonToken.END_ARRAY) {
                    reader.beginObject();
                    reader.nextName();
                    UUID uuid = UUID.fromString(reader.nextString());
                    reader.nextName();
                    String title = reader.nextString();
                    reader.nextName();
                    Date due = reader.peek() == JsonToken.NULL ? null : new Date(reader.nextLong());
                    if (due == null) reader.nextNull();
                    reader.nextName();
                    String text = reader.nextString();
                    Project project = new Project(uuid, title, due, text);
                    reader.nextName();
                    reader.beginArray();
                    while (reader.peek() != JsonToken.END_ARRAY)
                        project.addTag(UUID.fromString(reader.nextString()));
                    reader.endArray();
                    reader.nextName();
                    reader.beginArray();
                    while (reader.peek() != JsonToken.END_ARRAY)
                        project.addTask(UUID.fromString(reader.nextString()));
                    reader.endArray();
                    projects.put(project.getUUID(), project);
                    reader.endObject();
                }
                reader.endArray();
                reader.endObject();
                reader.close();
                return 0;
            } catch (FileNotFoundException fileNotFound) {
                return 1;
            } catch (IOException ioException) {
                return 1;
            }
        }
        return 0;
    }

    public int save(Context ctx) {
        if (!demoMode) {
            try {
                FileOutputStream fos = ctx.openFileOutput(FILE_NAME_TEMP, MODE_PRIVATE);
                OutputStreamWriter out = new OutputStreamWriter(fos);
                JsonWriter writer = new JsonWriter(out);
                writer.setIndent("\t");
                writer.beginObject();
                writer.name("tags").beginArray();
                for (Tag tag : tags.values()) {
                    writer.beginObject();
                    writer.name("id").value(tag.getUUID().toString());
                    writer.name("name").value(tag.getName());
                    writer.name("color").value(tag.getHexColor());
                    writer.endObject();
                }
                writer.endArray();
                writer.name("tasks").beginArray();
                for (Task task : tasks.values()) {
                    writer.beginObject();
                    writer.name("id").value(task.getUUID().toString());
                    writer.name("title").value(task.getTitle());
                    writer.name("completed").value(task.getCompleteStatus());
                    writer.name("size").value(task.getSize().toString());
                    writer.name("priority").value(task.getPriority().toString());
                    writer.name("text").value(task.getText());
                    writer.name("tags").beginArray();
                    for (UUID tags : task.getTags()) writer.value(tags.toString());
                    writer.endArray();
                    writer.name("blockers").beginArray();
                    for (UUID blockers : task.getBlockers()) writer.value(blockers.toString());
                    writer.endArray();
                    writer.endObject();
                }
                writer.endArray();
                writer.name("projects").beginArray();
                for (Project project : projects.values()) {
                    writer.beginObject();
                    writer.name("id").value(project.getUUID().toString());
                    writer.name("title").value(project.getTitle());
                    Date due = project.getDueDate();
                    writer.name("due").value(due != null ? due.getTime() : null);
                    writer.name("text").value(project.getText());
                    writer.name("tags").beginArray();
                    for (UUID tags : project.getTags()) writer.value(tags.toString());
                    writer.endArray();
                    writer.name("tasks").beginArray();
                    for (UUID tasks : project.getTasks()) writer.value(tasks.toString());
                    writer.endArray();
                    writer.endObject();
                }
                writer.endArray();
                writer.endObject();
                writer.flush();
                writer.close();
                File from = new File(ctx.getFilesDir(), FILE_NAME_TEMP);
                File to = new File(ctx.getFilesDir(), FILE_NAME);
                return from.renameTo(to) ? 0 : 1;
            } catch (FileNotFoundException fileNotFound) {
                return 1;
            } catch (IOException ioException) {
                return 1;
            }
        }
        return 0;
    }

    public boolean isStorageFilePresent(Context context) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + FILE_NAME;
        File file = new File(path);
        return file.exists();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateRestoredDueDate() {
        SimpleDateFormat df = new SimpleDateFormat("M/d/yyyy", Locale.ENGLISH);
        Date today = new Date(System.currentTimeMillis());
        long dateDiff = 0;
        try {
            dateDiff = Objects.requireNonNull(df.parse(df.format(today))).getTime() - Objects.requireNonNull(df.parse("3/30/2021")).getTime();
        } catch (ParseException ignored) { }
        long finalDateDiff = dateDiff;
        projects.values().forEach(p -> {
            Date due = p.getDueDate();
            if (due != null) {
                p.setDueDate(new Date(due.getTime() + finalDateDiff));
                projects.put(p.getUUID(), p);
            }
        });
    }

    public Project addProject(Project project) {
        return projects.put(project.getUUID(), project);
    }

    public void addTag(Tag tag) {
        tags.put(tag.getUUID(), tag);
    }

    public Task addTask(Task task) {
        return tasks.put(task.getUUID(), task);
    }

    public Project getProject(UUID projectUUID) {
        return projects.get(projectUUID);
    }

    public Map<UUID, Project> getProjects()
    {
        return projects;
    }

    public Tag getTag(UUID tagUUID) {
        return tags.get(tagUUID);
    }

    public Task getTask(UUID taskUUID) {
        return tasks.get(taskUUID);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<UUID> getValidBlockers(UUID taskUUID) {
        Set<UUID> retSet = new HashSet<>(getParentProject(taskUUID).getTasks());
        retSet.remove(taskUUID);
        retSet.removeAll(getAllDependants(taskUUID));
        return retSet.stream().map(this::getTask).sorted((b1, b2) -> b1.getTitle().compareTo(b2.getTitle()))
                .map(Task::getUUID).collect(Collectors.toList());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Set<UUID> getAllDependants(UUID taskUUID) {
        Set<UUID> allDependants = new HashSet<>();
        for (UUID id : getParentProject(taskUUID).getTasks()) {
            if (getTask(id).getBlockers().contains(taskUUID)) {
                allDependants.add(id);
                allDependants.addAll(getAllDependants(id));
            }
        }
        return allDependants;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<UUID> getOrderedTasks() {
        return tasks.values().stream().sorted(new TaskComparator()).map(Task::getUUID).collect(Collectors.toList());
    }

    /**
     * Ordered by due date, earliest first
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<UUID> getOrderedProjects() {
        return projects.values().stream().sorted(new ProjectComparator()).map(Project::getUUID).collect(Collectors.toList());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeProject(UUID projectUUID) {
        Iterator iterator = getProject(projectUUID).getTasks().iterator();
        while (iterator.hasNext()) {
            this.removeTask((UUID) iterator.next());
        }
        projects.remove(projectUUID);
    }

    public Project getParentProject(UUID taskUUID) {
        for (Map.Entry<UUID, Project> e : projects.entrySet()) {
            if (e.getValue().containsTask(taskUUID))
                return e.getValue();
        }
        throw new NoSuchElementException("No project contains the task");
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
        getParentProject(taskUUID).removeTask(taskUUID);
        return tasks.remove(taskUUID);
    }

    /**
     * @return 0: successful;
     * 1: no change;
     * 2: attempting to mark a blocked task as completed;
     * 3: the task is marked incomplete and it has incomplete blockers
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public int setTaskCompleted(UUID taskUUID, boolean completeStatus) {
        Task task = tasks.get(taskUUID);
        boolean isCompleted = task.getCompleteStatus();
        boolean isUnblocked = task.getBlockers().stream().allMatch(b -> tasks.get(b).getCompleteStatus());
        if (completeStatus) {
            if (isCompleted)
                return 1;
            if (!isUnblocked)
                return 2;
            task.setCompleteStatus(true);
        } else {
            if (!isCompleted)
                return 1;
            if (!isUnblocked) {
                task.setCompleteStatus(false);
                addTask(task);
                return 3;
            }
            task.setCompleteStatus(false);
        }
        addTask(task);
        return 0;
    }

    public static boolean isNightMode(Context ctx) {
        int nightModeFlags =
                ctx.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    public static void updateToolbarColor(Context ctx, Toolbar toolbar) {
        if (isNightMode(ctx)) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = ctx.getTheme();
            theme.resolveAttribute(R.attr.backgroundColor, typedValue, true);
            @ColorInt int color = typedValue.data;
            toolbar.setBackgroundColor(color);
        } else {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = ctx.getTheme();
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
            @ColorInt int color = typedValue.data;
            toolbar.setBackgroundColor(color);
        }
    }

    // Format: // 2021-03-24 17:12:03.311
    public Timestamp getTimestamp()
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp;
    }

    static class TaskAlphabetizer implements Comparator<Task> {
        public int compare(Task t1, Task t2) {
            return t1.getTitle().compareTo(t2.getTitle());
        }
    }

    static class TaskComparator implements Comparator<Task> {
        /**
         * Reversed ordered by number of completeness, priority, # of blockers, size
         */
        @RequiresApi(api = Build.VERSION_CODES.N)
        public int compare(Task t1, Task t2) {
            if (t1.getCompleteStatus() != t2.getCompleteStatus())
                return t1.getCompleteStatus() ? 1 : -1;
            if (t1.getPriority().ordinal() != t2.getPriority().ordinal())
                return t2.getPriority().ordinal() - t1.getPriority().ordinal();
            if (t1.getSize().ordinal() != t2.getSize().ordinal())
                return t2.getSize().ordinal() - t1.getSize().ordinal();
            return t1.getBlockersSize() - t1.getBlockersSize();
        }
    }

    static class ProjectComparator implements Comparator<Project> {
        /**
         * Reversed ordered completeness, due date
         */
        public int compare(Project p1, Project p2) {
            if (p1.getCompleteness() >= 1 && p2.getCompleteness() < 1)
                return 1;
            if (p1.getCompleteness() < 1 && p2.getCompleteness() >= 1)
                return -1;
            Date d1 = p1.getDueDate();
            Date d2 = p2.getDueDate();
            if (d1 == null || d2 == null)
                return d1 == null && d2 == null
                        ? Integer.compare(p2.getTasksSize(), p1.getTasksSize())
                        : d1 == null ? 1 : -1;
            return Long.compare(d1.getTime(), d2.getTime());
        }
    }
}

