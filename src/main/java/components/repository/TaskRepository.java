package components.repository;

import components.model.Task;
import java.util.*;
import java.io.*;
import java.time.LocalDate;

/**
 * TaskRepository handles all data storage operations for tasks.
 * This class manages the collection of tasks and provides CRUD operations.
 */
public class TaskRepository {
    
    // In-memory storage for tasks
    private List<Task> tasks;
    
    // Auto-increment ID counter
    private int nextId;
    
    // File path for persistence
    private static final String DATA_FILE = "data/tasks.txt";
    
    /**
     * Constructor - loads existing tasks from file or creates empty list
     */
    public TaskRepository() {
        this.tasks = new ArrayList<>();
        this.nextId = 1;
        loadTasksFromFile();
    }
    
    /**
     * Add a new task to the repository
     * @param task The task to add (ID will be auto-assigned if not set)
     * @return The added task with assigned ID
     */
    public Task addTask(Task task) {
        // Auto-assign ID if not set
        if (task.getId() <= 0) {
            task.setId(nextId++);
        } else {
            // Update nextId if needed
            if (task.getId() >= nextId) {
                nextId = task.getId() + 1;
            }
        }
        
        tasks.add(task);
        saveTasksToFile();
        return task;
    }
    
    /**
     * Get all tasks
     * @return List of all tasks
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks); // Return a copy to protect internal data
    }
    
    /**
     * Find a task by its ID
     * @param id The task ID to find
     * @return The task if found, null otherwise
     */
    public Task findById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }
    
    /**
     * Update an existing task
     * @param task The task with updated values
     * @return true if task was found and updated, false otherwise
     */
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
    
    /**
     * Delete a task by ID
     * @param id The ID of the task to delete
     * @return true if task was found and deleted, false otherwise
     */
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
    
    /**
     * Get the total number of tasks
     * @return Task count
     */
    public int getTaskCount() {
        return tasks.size();
    }
    
    /**
     * Get count of completed tasks
     * @return Completed task count
     */
    public int getCompletedCount() {
        int count = 0;
        for (Task task : tasks) {
            if (task.isCompleted()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Get count of pending tasks
     * @return Pending task count
     */
    public int getPendingCount() {
        return tasks.size() - getCompletedCount();
    }
    
    /**
     * Clear all tasks
     */
    public void clearAll() {
        tasks.clear();
        nextId = 1;
        saveTasksToFile();
    }
    
    // ========== FILE PERSISTENCE METHODS ==========
    
    /**
     * Save all tasks to file using character stream (Unit 1.5)
     */
    private void saveTasksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Task task : tasks) {
                // Format: id|title|completed|dueDate
                writer.write(formatTaskForFile(task));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }
    
    /**
     * Load tasks from file using character stream (Unit 1.5)
     */
    private void loadTasksFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return; // No file yet, start empty
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
    
    /**
     * Format a task for file storage
     */
    private String formatTaskForFile(Task task) {
        return task.getId() + "|" +
               escapeText(task.getTitle()) + "|" +
               task.isCompleted() + "|" +
               (task.getDueDate() != null ? task.getDueDate().toString() : "");
    }
    
    /**
     * Parse a task from file format
     */
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
    
    /**
     * Escape special characters for file storage
     */
    private String escapeText(String text) {
        if (text == null) return "";
        return text.replace("|", "\\|").replace("\n", "\\n");
    }
    
    /**
     * Unescape text from file storage
     */
    private String unescapeText(String text) {
        if (text == null) return "";
        return text.replace("\\|", "|").replace("\\n", "\n");
    }
}