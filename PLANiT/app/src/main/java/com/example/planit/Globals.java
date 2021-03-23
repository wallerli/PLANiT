package com.example.planit;

import android.graphics.Color;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class Globals{

    private static Globals globals_instance = null;
    private final Map<UUID, Project> projects = new HashMap<UUID, Project>();
    private final Map<UUID, Tag> tags = new HashMap<UUID, Tag>();
    private final Map<UUID, Task> tasks = new HashMap<UUID, Task>();

    private UUID demoProjectUUID = null;
    private UUID demoTaskUUID = null;

    private Globals() {
//        projects.put(null, new Project("New Project"));
//        tags.put(null, new Tag("New Tag"));
//        tasks.put(null, new Task("New Task", Size.MEDIUM, Priority.MODERATE));
        setupDummyObjects();
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
        return tasks.remove(taskUUID);
    }

    /**
     * Title and Text are generated from https://www.blindtextgenerator.com/lorem-ipsum
     */
    public void setupDummyObjects() {
        this.tags.clear();
        this.projects.clear();
        this.tasks.clear();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.getDefault());
        Tag tag1 = new Tag("Reading", Color.rgb(255, 0, 0));
        Tag tag2 = new Tag("Do first", Color.rgb(0, 255, 0));
        Tag tag3 = new Tag("Team work");
        addTag(tag1);
        addTag(tag2);
        addTag(tag3);

        Project project1 = new Project("ProjectOneTitle lus ut perspiciatis");
        project1.addTag(tag1.getUUID());
        project1.addTag(tag3.getUUID());
        project1.setText("" +
                        "ProjectOneDescription lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus.\n" +
                        "\n" +
                        "Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante."
                        );
        try {
            project1.setDueDate(df.parse("3/23/2022 23:59"));
        } catch (ParseException ignored) {}

        Task task1 = new Task("TaskOneTitle et harum quidem", Size.MEDIUM, Priority.HIGH);
        task1.addTag(tag1.getUUID());
        task1.setText("" +
                        "TaskOneDescription donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem.\n" +
                        "\n" +
                        "Nulla consequat massa quis enim.\n" +
                        "\n" +
                        "Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu.\n" +
                        "\n" +
                        "In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium.\n" +
                        "\n" +
                        "Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus." +
                        "");
        Task task2 = new Task("TaskTwoTitle taque earum rerum hic", Size.LARGE, Priority.CRITICAL);
        task2.addTag(tag2.getUUID());
        task2.setText("" +
                        "TaskTwoDescription curabitur ligula sapien, tincidunt non, euismod vitae, posuere imperdiet, leo. Maecenas malesuada.\n" +
                        "\n" +
                        "Praesent congue erat at massa. Sed cursus turpis vitae tortor. Donec posuere vulputate arcu.\n" +
                        "\n" +
                        "Phasellus accumsan cursus velit.\n" +
                        "\n" +
                        "Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Sed aliquam, nisi quis porttitor congue, elit erat euismod orci, ac placerat dolor lectus quis orci.\n" +
                        "\n" +
                        "Phasellus consectetuer vestibulum elit. Aenean tellus metus, bibendum sed, posuere ac, mattis non, nunc.\n" +
                        "\n" +
                        "Vestibulum fringilla pede sit amet augue. In turpis. Pellentesque posuere. Praesent turpis.\n" +
                        "\n" +
                        "Aenean posuere, tortor sed cursus feugiat, nunc augue blandit nunc, eu sollicitudin urna dolor sagittis lacus. Donec elit libero, sodales nec, volutpat a, suscipit non, turpis.\n" +
                        "\n" +
                        "Nullam sagittis. Suspendisse pulvinar, augue ac venenatis condimentum, sem libero volutpat nibh, nec pellentesque velit pede quis nunc.\n" +
                        "\n" +
                        "Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Fusce id purus. Ut varius tincidunt libero. Phasellus dolor. Maecenas vestibulum mollis" +
                        "");
        task2.addBlocker(task1.getUUID());
        Task task3 = new Task("TaskThreeTitle t vero eos et accusamus", Size.LARGE, Priority.CRITICAL);
        task3.addTag(tag1.getUUID());
        task3.setText("" +
                        "TaskThreeDescription am libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus.\n" +
                        "\n" +
                        "Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae.\n" +
                        "\n" +
                        "Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat." +
                        "");
        task3.setCompleteStatus(true);
        project1.addTask(task1.getUUID());
        project1.addTask(task2.getUUID());
        project1.addTask(task3.getUUID());
        addProject(project1);
        addTask(task1);
        addTask(task2);
        addTask(task3);



        Project project2 = new Project("ProjectTwoTitle uis autem vel eum iure");
        project2.setText("" +
                        "ProjectTwoDescription hasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem.\n" +
                        "\n" +
                        "Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc, quis gravida magna mi a libero. Fusce vulputate eleifend sapien."
                        );
        try {
            project2.setDueDate(df.parse("03/23/2021 05:12"));
        } catch (ParseException ignored) {}

        Task task4 = new Task("TaskFourTitle ed ut perspiciatis unde omnis", Size.SMALL, Priority.MODERATE);
        task4.addTag(tag2.getUUID());
        task4.setText("" +
                        "TaskFourDescription orem ipsum dolor sit amet, consectetuer adipiscing elit.\n" +
                        "\n" +
                        "Aenean commodo ligula eget dolor. Aenean massa.\n" +
                        "\n" +
                        "Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.\n" +
                        "\n" +
                        "Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem.\n" +
                        "\n" +
                        "Nulla consequat massa quis enim." +
                        "");
        project2.addTask(task4.getUUID());
        addProject(project2);
        addTask(task4);



        demoProjectUUID = project1.getUUID();
        demoTaskUUID = task2.getUUID();
    }

    /**
     * @return the demo project UUID as string
     */
    public String getProject() {
        return demoProjectUUID.toString();
    }

    /**
     * @return the demo task UUID as string
     */
    public String getTask() {
        return demoTaskUUID.toString();
    }
}