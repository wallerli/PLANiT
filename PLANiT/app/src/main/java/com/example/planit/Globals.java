package com.example.planit;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.sql.Timestamp;

public class Globals {

    private static Globals globals_instance = null;
    private final Map<UUID, Project> projects = new HashMap<UUID, Project>();
    private final Map<UUID, Tag> tags = new HashMap<UUID, Tag>();
    private final Map<UUID, Task> tasks = new HashMap<UUID, Task>();

    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_TEXT_LENGTH = 500;
    public static final int MAX_TAG_LENGTH = 20;

    private Globals() {
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
        return new ArrayList<>(retSet);
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

//    public List<UUID> getProjects() { return new ArrayList<>(projects.keySet()); }

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

    public void setupDummyObjects() {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.getDefault());
        Tag tag0 = new Tag("Work", Color.rgb(112, 87, 255));
        Tag tag1 = new Tag("CSCI 5115", Color.rgb(235, 135, 57));
        Tag tag2 = new Tag("Spring Semester", Color.rgb(0, 134, 114));
        Tag tag3 = new Tag("Group-Work", Color.rgb(186, 180, 38));
//        Tag tag4 = new Tag("When you have “free time” you can do this");
        Tag tag4 = new Tag("“free time” only");  // The original one looks too long
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
                "umn.edu" +
                "");
        project1.addTask(task3.getUUID());
        addProject(project1);
        addTask(task3);


        Project project2 = new Project("Plan for graduation!!! 🎓");
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
//        task4.setCompleteStatus(true);
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

        Project project5 = new Project("Prepare for An Exam");
        project5.setText(""+"Prepare for an exam on this Friday"+"");
        try{
            project5.setDueDate(df.parse("4/2/2021 13:00"));
        }
        catch (ParseException ignored){}

        Task task10 = new Task("Review Lecture Notes", Size.LARGE, Priority.HIGH);
        task10.setCompleteStatus(true);
        task10.setText("Review lecture notes from January to March");

        Task task11 = new Task("Finish practice exam", Size.SMALL, Priority.MODERATE);
        task11.setCompleteStatus(false);
        task11.setText("Not so hard but need to complete before exam");
        task11.addBlocker(task10.getUUID());
        task11.addTag(tag2.getUUID());

        project5.addTask(task10.getUUID());
        project5.addTask(task11.getUUID());
        addProject(project5);
        addTask(task10);
        addTask(task11);

        Project project6 = new Project("🐻 Draw a Bear");
        project6.setText("" +"Draw a bear on a piece of paper\n"+
                            "Our bear does not have a nose!!!" + "");

        Task task12 = new Task("Draw Head", Size.TINY, Priority.CRITICAL);
        task12.setCompleteStatus(true);
        task12.setText("Draw a round head for our bear");

        Task task13 = new Task("Draw Eyes", Size.SMALL, Priority.LOW);
        task13.setCompleteStatus(false);
        task13.setText("Draw a pair of eyes for our bear");
        task13.addBlocker(task12.getUUID());

        Task task14 = new Task("Draw Mouth", Size.TINY, Priority.LOW);
        task14.setCompleteStatus(false);
        task14.setText("Draw a mouth for our bear");
        task14.addBlocker(task12.getUUID());
        task14.addBlocker(task13.getUUID());

        Task task15 = new Task("Draw Left Ear", Size.TINY, Priority.LOW);
        task15.setCompleteStatus(true);
        task15.setText("Draw one ear for our bear");
        task15.addBlocker(task12.getUUID());

        Task task16 = new Task("Draw Right Ear", Size.TINY, Priority.LOW);
        task16.setCompleteStatus(false);
        task16.setText("Draw another ear for our bear");
        task16.addBlocker(task12.getUUID());

        Task task17 = new Task("Draw Body", Size.SMALL, Priority.HIGH);
        task17.setCompleteStatus(true);
        task17.setText("Draw the body for our bear");
        task17.addBlocker(task12.getUUID());

        Task task18 = new Task("Draw Left Arm", Size.TINY, Priority.LOW);
        task18.setCompleteStatus(false);
        task18.setText("Draw the left arm for our bear");
        task18.addBlocker(task12.getUUID());
        task18.addBlocker(task17.getUUID());

        Task task19 = new Task("Draw Right Arm", Size.TINY, Priority.LOW);
        task19.setCompleteStatus(false);
        task19.setText("Draw the right arm for our bear");
        task19.addBlocker(task12.getUUID());
        task19.addBlocker(task17.getUUID());

        Task task20 = new Task("Draw Left Leg", Size.TINY, Priority.LOW);
        task20.setCompleteStatus(false);
        task20.setText("Draw the left leg for our bear");
        task20.addBlocker(task12.getUUID());
        task20.addBlocker(task17.getUUID());

        Task task21 = new Task("Draw Right Leg", Size.TINY, Priority.LOW);
        task21.setCompleteStatus(true);
        task21.setText("Draw the right leg for our bear");
        task21.addBlocker(task12.getUUID());
        task21.addBlocker(task17.getUUID());

        project6.addTask(task12.getUUID());
        project6.addTask(task13.getUUID());
        project6.addTask(task14.getUUID());
        project6.addTask(task15.getUUID());
        project6.addTask(task16.getUUID());
        project6.addTask(task17.getUUID());
        project6.addTask(task18.getUUID());
        project6.addTask(task19.getUUID());
        project6.addTask(task20.getUUID());
        project6.addTask(task21.getUUID());
        addProject(project6);
        addTask(task12);
        addTask(task13);
        addTask(task14);
        addTask(task15);
        addTask(task16);
        addTask(task17);
        addTask(task18);
        addTask(task19);
        addTask(task20);
        addTask(task21);

        Project project7 = new Project("Buy an Umbrella on Amazon 🌂");
        project7.setText("" +
                "Buy an umbrella with a moderate price on Amazon" +
                "");
        try {
            project7.setDueDate(df.parse("04/15/2021 23:59"));
        } catch (ParseException ignored) {}

        Task task22 = new Task("Download Amazon", Size.TINY, Priority.CRITICAL);
        task22.setCompleteStatus(true);
        task22.setText("Download amazon on app store");

        Task task23 = new Task("Search Umbrella", Size.SMALL, Priority.MODERATE);
        task23.setCompleteStatus(true);
        task23.setText("Search a list of umbrella using the keyword: \"umbrella\"");
        task23.addBlocker(task22.getUUID());

        Task task24 = new Task("Compare Prices among Umbrellas", Size.HUGE, Priority.CRITICAL);
        task24.setCompleteStatus(true);
        task24.setText("Select and compare the price of several umbrellas");
        task24.addBlocker(task23.getUUID());

        Task task25 = new Task("Add Umbrella to Cart", Size.SMALL, Priority.LOW);
        task25.setCompleteStatus(true);
        task25.setText("Add the one with fair price and good reviews to shopping cart");
        task25.addBlocker(task24.getUUID());

        Task task26 = new Task("Pay for the Umbrella", Size.TINY, Priority.LOW);
        task26.setCompleteStatus(false);
        task26.addBlocker(task25.getUUID());

        project7.addTask(task22.getUUID());
        project7.addTask(task23.getUUID());
        project7.addTask(task24.getUUID());
        project7.addTask(task25.getUUID());
        project7.addTask(task26.getUUID());
        addProject(project7);
        addTask(task22);
        addTask(task23);
        addTask(task24);
        addTask(task25);
        addTask(task26);

        Project project8 = new Project("Do the Laundry");
        project8.setText("Do the laundry before the end of this week!" +
                "" +
                "");
        try {
            project8.setDueDate(df.parse("04/04/2021 23:59"));
        } catch (ParseException ignored) {}

        Task task27 = new Task("Open the Lid of Washer", Size.TINY, Priority.LOW);
        task27.setCompleteStatus(true);

        Task task28 = new Task("Put Delicate Clothes into Laundry Bags", Size.MEDIUM, Priority.HIGH);
        task28.setCompleteStatus(false);
        task28.setText("Put delicate clothes into laundry bags");

        Task task29 = new Task("Put All Clothes into Washer", Size.HUGE, Priority.LOW);
        task29.setCompleteStatus(false);

        Task task30 = new Task("Add detergent", Size.SMALL, Priority.LOW);
        task30.setCompleteStatus(false);
        task30.addBlocker(task29.getUUID());

        Task task31 = new Task("Set the timer for washer", Size.SMALL, Priority.LOW);
        task31.setText("Set the timer of washer based on the conditions");
        task31.addBlocker(task30.getUUID());
        task31.setCompleteStatus(false);

        Task task32 = new Task("Put Everything into Dryer", Size.HUGE, Priority.LOW);
        task32.setText("Place all clothes into dryer after washer stopping");
        task32.setCompleteStatus(false);
        task32.addBlocker(task31.getUUID());

        Task task33 = new Task("Set the timer for Dryer", Size.SMALL, Priority.LOW);
        task33.setText("Set the timer of dryer based on the conditions");
        task33.setCompleteStatus(false);
        task33.addBlocker(task32.getUUID());

        Task task34 = new Task("Take everything out of the dryer", Size.HUGE, Priority.LOW);
        task34.setText("Take everything out of the dryer once it is done");
        task34.setCompleteStatus(false);
        task34.addBlocker(task33.getUUID());

        project8.addTask(task27.getUUID());
        project8.addTask(task28.getUUID());
        project8.addTask(task29.getUUID());
        project8.addTask(task30.getUUID());
        project8.addTask(task31.getUUID());
        project8.addTask(task32.getUUID());
        project8.addTask(task33.getUUID());
        project8.addTask(task34.getUUID());
        addProject(project8);
        addTask(task27);
        addTask(task28);
        addTask(task29);
        addTask(task30);
        addTask(task31);
        addTask(task32);
        addTask(task33);
        addTask(task34);
    }

    // Format: // 2021-03-24 17:12:03.311
    public Timestamp getTimestamp()
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp;
    }

    static class TaskComparator implements Comparator<Task> {
        /**
         * Reversed ordered by number of completeness, priority, # of blockers, size
         */
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

