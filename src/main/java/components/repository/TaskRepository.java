package components.repository;

import components.model.Task;
import java.util.*;
import java.io.*;
import java.time.LocalDate;

public class TaskRepository {

    private List<Task> tasks;
    private int nextId;
    private static final String DATA_FILE = "data/tasks.txt";

    public TaskRepository() {
        this.tasks = new ArrayList<>();
        this.nextId = 1;
        loadTasksFromFile();
    }

    public Task addTask(Task task) {
        if (task.getId() <= 0) {
            task.setId(nextId++);
        } else {
            if (task.getId() >= nextId) {
                nextId = task.getId() + 1;
            }
        }
        tasks.add(task);
        saveTasksToFile();
        return task;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public Task findById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    public boolean updateTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.set(i, task);
                saveTasksToFile();
                return true;
            }
        }
        return false;
    }

    public boolean deleteTask(int id) {
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getId() == id) {
                iterator.remove();
                saveTasksToFile();
                return true;
            }
        }
        return false;
    }

    // ADD THIS NEW METHOD
    public boolean markTaskCompleted(int id) {
        Task task = findById(id);
        if (task != null) {
            task.markCompleted();
            updateTask(task);
            return true;
        }
        return false;
    }

    public int getTaskCount() {
        return tasks.size();
    }

    public int getCompletedCount() {
        int count = 0;
        for (Task task : tasks) {
            if (task.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    public int getPendingCount() {
        return tasks.size() - getCompletedCount();
    }

    public void clearAll() {
        tasks.clear();
        nextId = 1;
        saveTasksToFile();
    }

    private void saveTasksToFile() {
        try {
            // Create data folder if it doesn't exist
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdir();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
                for (Task task : tasks) {
                    writer.write(formatTaskForFile(task));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    private void loadTasksFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = parseTaskFromFile(line);
                if (task != null) {
                    tasks.add(task);
                    if (task.getId() >= nextId) {
                        nextId = task.getId() + 1;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
    }

    private String formatTaskForFile(Task task) {
        return task.getId() + "|" +
                escapeText(task.getTitle()) + "|" +
                task.isCompleted() + "|" +
                (task.getDueDate() != null ? task.getDueDate().toString() : "");
    }

    private Task parseTaskFromFile(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 3) {
                int id = Integer.parseInt(parts[0]);
                String title = unescapeText(parts[1]);
                boolean completed = Boolean.parseBoolean(parts[2]);

                Task task;
                if (parts.length >= 4 && !parts[3].isEmpty()) {
                    LocalDate dueDate = LocalDate.parse(parts[3]);
                    task = new Task(id, title, dueDate);
                } else {
                    task = new Task(id, title);
                }

                if (completed) {
                    task.markCompleted();
                }
                return task;
            }
        } catch (Exception e) {
            System.err.println("Error parsing task: " + line);
        }
        return null;
    }

    private String escapeText(String text) {
        if (text == null)
            return "";
        return text.replace("|", "\\|").replace("\n", "\\n");
    }

    private String unescapeText(String text) {
        if (text == null)
            return "";
        return text.replace("\\|", "|").replace("\\n", "\n");
    }
}