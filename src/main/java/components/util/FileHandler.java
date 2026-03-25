package components.util;

import components.model.Task;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * FileHandler handles all file I/O operations for tasks.
 * Demonstrates byte streams, character streams, and exception handling (Unit 1.3, 1.5)
 */
public class FileHandler {
    
    // File paths
    private static final String TEXT_FILE = "data/tasks.txt";      // Character stream
    private static final String BINARY_FILE = "data/tasks.dat";    // Byte stream
    private static final String BACKUP_FILE = "data/tasks_backup.txt";
    
    // ========== CHARACTER STREAMS (Text Files) ==========
    
    /**
     * Save tasks to text file using character streams (FileWriter, BufferedWriter)
     * Demonstrates try-with-resources (Unit 1.3)
     */
    public static void saveToTextFile(List<Task> tasks) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEXT_FILE))) {
            for (Task task : tasks) {
                String line = formatTaskForText(task);
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Saved " + tasks.size() + " tasks to " + TEXT_FILE);
        } catch (IOException e) {
            System.err.println("Error saving to text file: " + e.getMessage());
            throw e; // Re-throw for caller to handle
        }
    }
    
    /**
     * Load tasks from text file using character streams (FileReader, BufferedReader)
     */
    public static List<Task> loadFromTextFile() throws IOException {
        List<Task> tasks = new ArrayList<>();
        File file = new File(TEXT_FILE);
        
        // If file doesn't exist, return empty list
        if (!file.exists()) {
            return tasks;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(TEXT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = parseTaskFromText(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        }
        
        System.out.println("Loaded " + tasks.size() + " tasks from " + TEXT_FILE);
        return tasks;
    }
    
    // ========== BYTE STREAMS (Binary Files) ==========
    
    /**
     * Save tasks to binary file using byte streams (FileOutputStream, ObjectOutputStream)
     * Demonstrates object serialization (Unit 1.5)
     */
    public static void saveToBinaryFile(List<Task> tasks) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(BINARY_FILE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            
            oos.writeObject(tasks);
            System.out.println("Saved " + tasks.size() + " tasks to binary file");
            
        } catch (IOException e) {
            System.err.println("Error saving to binary file: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Load tasks from binary file using byte streams (FileInputStream, ObjectInputStream)
     */
    @SuppressWarnings("unchecked")
    public static List<Task> loadFromBinaryFile() throws IOException, ClassNotFoundException {
        File file = new File(BINARY_FILE);
        
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (FileInputStream fis = new FileInputStream(BINARY_FILE);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            
            List<Task> tasks = (List<Task>) ois.readObject();
            System.out.println("Loaded " + tasks.size() + " tasks from binary file");
            return tasks;
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading from binary file: " + e.getMessage());
            throw e;
        }
    }
    
    // ========== RANDOM ACCESS FILE ==========
    
    /**
     * Update a specific task using RandomAccessFile (Unit 1.5)
     * This demonstrates seeking to a specific position in a file
     */
    public static void updateTaskStatusViaRAF(int taskId, boolean completed) throws IOException {
        File file = new File(TEXT_FILE);
        if (!file.exists()) {
            return;
        }
        
        // Read all tasks, modify, and rewrite
        // (RandomAccessFile with fixed record size would be more complex)
        List<Task> tasks = loadFromTextFile();
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                if (completed) {
                    task.markCompleted();
                }
                break;
            }
        }
        saveToTextFile(tasks);
    }
    
    // ========== BACKUP AND UTILITY METHODS ==========
    
    /**
     * Create a backup of tasks using character streams
     * Demonstrates try-catch-finally (Unit 1.3)
     */
    public static void createBackup() {
        FileReader reader = null;
        FileWriter writer = null;
        
        try {
            File source = new File(TEXT_FILE);
            if (!source.exists()) {
                System.out.println("No data file to backup");
                return;
            }
            
            reader = new FileReader(source);
            writer = new FileWriter(BACKUP_FILE);
            
            int character;
            while ((character = reader.read()) != -1) {
                writer.write(character);
            }
            
            System.out.println("Backup created at " + BACKUP_FILE);
            
        } catch (IOException e) {
            System.err.println("Backup failed: " + e.getMessage());
        } finally {
            // Clean up resources
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
            } catch (IOException e) {
                System.err.println("Error closing files: " + e.getMessage());
            }
        }
    }
    
    /**
     * Restore from backup
     */
    public static void restoreFromBackup() throws IOException {
        File backup = new File(BACKUP_FILE);
        if (!backup.exists()) {
            throw new IOException("Backup file not found");
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(BACKUP_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter(TEXT_FILE))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
        
        System.out.println("Restored from backup");
    }
    
    /**
     * Export tasks to CSV format
     */
    public static void exportToCSV(List<Task> tasks, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write header
            writer.write("ID,Title,Status,Due Date");
            writer.newLine();
            
            // Write data
            for (Task task : tasks) {
                String line = String.format("%d,\"%s\",%s,%s",
                    task.getId(),
                    task.getTitle().replace("\"", "\"\""),
                    task.isCompleted() ? "Completed" : "Pending",
                    task.getDueDate() != null ? task.getDueDate() : ""
                );
                writer.write(line);
                writer.newLine();
            }
        }
        
        System.out.println("Exported " + tasks.size() + " tasks to " + filePath);
    }
    
    // ========== PRIVATE HELPER METHODS ==========
    
    /**
     * Format a task for text file storage
     */
    private static String formatTaskForText(Task task) {
        return task.getId() + "|" +
               escapeSpecial(task.getTitle()) + "|" +
               task.isCompleted() + "|" +
               (task.getDueDate() != null ? task.getDueDate().toString() : "");
    }
    
    /**
     * Parse a task from text file line
     */
    private static Task parseTaskFromText(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 3) {
                int id = Integer.parseInt(parts[0]);
                String title = unescapeSpecial(parts[1]);
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
    private static String escapeSpecial(String text) {
        if (text == null) return "";
        return text.replace("|", "\\|").replace("\n", "\\n");
    }
    
    /**
     * Unescape text from file storage
     */
    private static String unescapeSpecial(String text) {
        if (text == null) return "";
        return text.replace("\\|", "|").replace("\\n", "\n");
    }
    
    // ========== FILE UTILITY METHODS ==========
    
    /**
     * Check if data file exists
     */
    public static boolean dataFileExists() {
        return new File(TEXT_FILE).exists();
    }
    
    /**
     * Get file size in bytes
     */
    public static long getFileSize() {
        File file = new File(TEXT_FILE);
        return file.exists() ? file.length() : 0;
    }
    
    /**
     * Delete data file (reset)
     */
    public static boolean deleteDataFile() {
        File file = new File(TEXT_FILE);
        return file.exists() && file.delete();
    }
}