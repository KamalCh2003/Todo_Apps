package ui;

import model.Task;
import repository.TaskRepository;

import java.util.List;
import java.util.Scanner;

public class TaskUI {

    private static TaskRepository taskRepo = new TaskRepository();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Welcome to ToDo App ===");
        boolean exit = false;

        while (!exit) {
            showMenu();
            int choice = getChoice();
            switch (choice) {
                case 1:
                    addTask();
                    break;
                case 2:
                    viewTasks();
                    break;
                case 3:
                    markTaskCompleted();
                    break;
                case 4:
                    deleteTask();
                    break;
                case 5:
                    System.out.println("Exiting... Goodbye!");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Add Task");
        System.out.println("2. View Tasks");
        System.out.println("3. Mark Task Completed");
        System.out.println("4. Delete Task");
        System.out.println("5. Exit");
        System.out.print("Enter choice: ");
    }

    private static int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1; // Invalid input
        }
    }

    private static void addTask() {
        System.out.print("Enter task title: ");
        String title = scanner.nextLine();

        // Generate ID based on current tasks
        int id = taskRepo.getAllTasks().size() + 1;
        Task task = new Task(id, title);
        taskRepo.addTask(task);

        System.out.println("Task added successfully!");
    }

    private static void viewTasks() {
        List<Task> tasks = taskRepo.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        System.out.println("\n--- Your Tasks ---");
        for (Task t : tasks) {
            System.out.println(t.getId() + ". " + t.getTitle() +
                    " [Completed: " + t.isCompleted() + "]");
        }
    }

    private static void markTaskCompleted() {
        System.out.print("Enter task ID to mark as completed: ");
        int id = getChoice();
        boolean success = taskRepo.markTaskCompleted(id);
        if (success) {
            System.out.println("Task marked as completed!");
        } else {
            System.out.println("Task not found.");
        }
    }

    private static void deleteTask() {
        System.out.print("Enter task ID to delete: ");
        int id = getChoice();
        boolean success = taskRepo.deleteTask(id);
        if (success) {
            System.out.println("Task deleted successfully!");
        } else {
            System.out.println("Task not found.");
        }
    }
}