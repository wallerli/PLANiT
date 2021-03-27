package com.example.planit;

import android.graphics.Color;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class Globals{

    private static Globals globals_instance = null;
    private final Map<UUID, Project> projects = new HashMap<UUID, Project>();
    private final Map<UUID, Tag> tags = new HashMap<UUID, Tag>();
    private final Map<UUID, Task> tasks = new HashMap<UUID, Task>();

    public UUID demoProjectUUID;
    public UUID demoTaskUUID;

    private Globals() {
        projects.put(null, new Project(""));
        tags.put(null, new Tag(""));
        tasks.put(null, new Task("", Size.MEDIUM, Priority.MODERATE));
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

    public Project getProject() {
        return projects.get(null);
    }

    public Project getProject(UUID projectUUID) {
        return projects.get(projectUUID);
    }

    public Tag getTag() {
        return tags.get(null);
    }

    public Tag getTag(UUID tagUUID) {
        return tags.get(tagUUID);
    }

    public Task getTask() {
        return tasks.get(null);
    }

    public Task getTask(UUID taskUUID) {
        return tasks.get(taskUUID);
    }

    public List<UUID> getTasks() { return new ArrayList<>(tasks.keySet()); }

    public List<UUID> getProjects() { return new ArrayList<>(projects.keySet()); }

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
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.getDefault());
        Tag tag0 = new Tag("Work", Color.rgb(112, 87, 255));
        Tag tag1 = new Tag("CSCI 5115", Color.rgb(235, 135, 57));
        Tag tag2 = new Tag("Spring Semester", Color.rgb(0, 134, 114));
        Tag tag3 = new Tag("Group-Work", Color.rgb(236, 230, 88));
        Tag tag4 = new Tag("When you have “free time” you can do this");
        Tag tag5 = new Tag("\uD83E\uDD73", Color.rgb(0, 117, 202));
        Tag tag6 = new Tag("MOST IMPORTANT", Color.rgb(182, 2, 5));
        addTag(tag0);
        addTag(tag1);
        addTag(tag2);
        addTag(tag3);
        addTag(tag4);
        addTag(tag5);
        addTag(tag6);

        Project project0 = new Project("Final Project App Development");
        project0.addTag(tag1.getUUID());
        project0.addTag(tag2.getUUID());
        project0.addTag(tag3.getUUID());
        project0.setText("" +
                "Work in a group of 6 to create a functioning prototype of an app. Multiple tasks associated with this that are not equally weighted. Milestones along the way need to be completed in order." +
                "");
        try {
            project0.setDueDate(df.parse("4/27/2021 12:00"));
        } catch (ParseException ignored) {}

        Task task0 = new Task("Create a User Study Protocol", Size.SMALL, Priority.HIGH);
        task0.addTag(tag3.getUUID());
        task0.setCompleteStatus(true);
        task0.setText("" +
                "Needs to include a consent form, interview protocols, online questionnaire with demographic information. Main themes should be decided as a group, but individual work can be done to complete." +
                "");

        Task task1 = new Task("Milestone 1 - Poster", Size.MEDIUM, Priority.HIGH);
        task1.addTag(tag3.getUUID());
        task1.setCompleteStatus(true);
        task1.setText("" +
                "Create a poster that highlights our groups current progress with developing an app idea. Focus on process and results.\n" +
                "Key grading points:\n" +
                "* Accurately describe protocol, data collection and analysis\n" +
                "* 5 key user themes\n" +
                "* Single coherent app idea\n" +
                "* Utilize visuals to explain a narrative\n" +
                "* Clean design and layout that is easily understandable" +
                "");
        task1.addBlocker(task0.getUUID());

        Task task2 = new Task("Milestone 2 Demonstration", Size.HUGE, Priority.HIGH);
        task2.addTag(tag3.getUUID());
        task2.addTag(tag4.getUUID());
        task2.setText("" +
                "A functional prototype of our app, PLANit, needs to be complete for a demonstration. See Github for to-dos and to keep on track. \n" +
                "Key grading points:\n" +
                "* Implement most of the details previously conceptualized\n" +
                "* Explain changes\n" +
                "* Working prototype\n" +
                "* Design is audience specific and friendly\n" +
                "* Sample tasks to showcase the app (;\n" +
                "* Good pitch!" +
                "");
        task2.addBlocker(task0.getUUID());
        task2.addBlocker(task1.getUUID());

        project0.addTask(task0.getUUID());
        project0.addTask(task1.getUUID());
        project0.addTask(task2.getUUID());
        addProject(project0);
        addTask(task0);
        addTask(task1);
        addTask(task2);


        Project project1 = new Project("Update Company Website");
        project1.setText("" +
                "Include the bios of the new 2021 employees. Find them on the Twitter page (@companyteam_twitter)" +
                "");
        project1.addTag(tag0.getUUID());
        try {
            project1.setDueDate(df.parse("03/01/2021 23:59"));
        } catch (ParseException ignored) {}

        Task task3 = new Task("Make a list of employees missing from “About us” page", Size.MEDIUM, Priority.LOW);
        task3.addTag(tag0.getUUID());
        task3.addTag(tag4.getUUID());
        task3.setText("" +
                "www.mycompanyweb/about/us.com" +
                "");
        project1.addTask(task3.getUUID());
        addProject(project1);
        addTask(task3);


        Project project2 = new Project("Plan for graduation!!!");
        project2.setText("" +
                "The time has come, make sure you’re prepared!" +
                "");
        project2.addTag(tag2.getUUID());
        project2.addTag(tag5.getUUID());
        try {
            project2.setDueDate(df.parse("05/17/2021 23:59"));
        } catch (ParseException ignored) {}

        Task task4 = new Task("Apply to graduate", Size.SMALL, Priority.CRITICAL);
        task4.addTag(tag2.getUUID());
        task4.addTag(tag5.getUUID());
        task4.setCompleteStatus(true);
        task4.setText("" +
                "Email advisor to set up a meeting.\n" +
                "In the meeting:\n" +
                "* Discuss requirements\n" +
                "* Confirm Major and Minors will be complete\n" +
                "* Make sure you're on the list to be approved in May!\n" +
                "* Ask for written confirmation" +
                "");

        Task task5 = new Task("Make graduation slide", Size.TINY, Priority.MODERATE);
        task5.addTag(tag4.getUUID());
        task5.addTag(tag2.getUUID());
        task5.setText("" +
                "Submit picture of yourself." +
                "");
        task5.addBlocker(task4.getUUID());

        Task task6 = new Task("Order graduation apparel", Size.TINY, Priority.LOW);
        task6.addTag(tag4.getUUID());
        task6.addTag(tag2.getUUID());
        task6.setText("" +
                "Cap, Gown, Stole" +
                "");

        Task task7 = new Task("Pass your classes", Size.HUGE, Priority.CRITICAL);
        task7.addTag(tag2.getUUID());
        task7.addTag(tag6.getUUID());
        project2.addTask(task4.getUUID());
        project2.addTask(task5.getUUID());
        project2.addTask(task6.getUUID());
        project2.addTask(task7.getUUID());
        addProject(project2);
        addTask(task4);
        addTask(task5);
        addTask(task6);
        addTask(task7);


        Project project3 = new Project("Analyze Company Data");
        project3.setText("" +
                "Find ways to utilize past company data that is already being stored. My boss wants to increase sales, find new consumer trends to impress her." +
                "");
        project3.addTag(tag0.getUUID());
        project3.addTag(tag4.getUUID());
        addProject(project3);


        Project project4 = new Project("Android Studio");
        project4.setText("" +
                "Accurately complete the class homework assignments to gain a basic understanding of app development in Android Studio." +
                "");
        project4.addTag(tag1.getUUID());
        project4.addTag(tag2.getUUID());
        try {
            project2.setDueDate(df.parse("03/01/2021 23:59"));
        } catch (ParseException ignored) {}

        Task task8 = new Task("Android Studio Homework 1", Size.MEDIUM, Priority.MODERATE);
        task8.addTag(tag1.getUUID());
        task8.setCompleteStatus(true);
        task8.setText("" +
                "https://canvas.umn.edu/courses/217930/assignments/1672214?module_item_id=5519210" +
                "");

        Task task9 = new Task("Android Studio Homework 2", Size.MEDIUM, Priority.MODERATE);
        task9.addTag(tag1.getUUID());
        task9.setCompleteStatus(true);
        task9.setText("" +
                "https://canvas.umn.edu/courses/217930/assignments/1672215?module_item_id=5519230" +
                "");
        task9.addBlocker(task8.getUUID());
        project4.addTask(task8.getUUID());
        project4.addTask(task9.getUUID());
        addProject(project4);
        addTask(task8);
        addTask(task9);

        demoProjectUUID = project0.getUUID();
        demoTaskUUID = task2.getUUID();
    }
}