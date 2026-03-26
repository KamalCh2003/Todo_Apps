package components.model;

import java.time.LocalDate;

public class Task {
    private int id;               // Unique task ID
    private String title;         // Task title or description
    private boolean completed;    // Status: true = completed
    private LocalDate dueDate;    // Optional due date

    // Constructor without due date
    public Task(int id, String title) {
        this.id = id;
        this.title = title;
        this.completed = false;
        this.dueDate = null;
    }

    // Constructor with due date
    public Task(int id, String title, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.completed = false;
        this.dueDate = dueDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // Mark task as completed
    public void markCompleted() {
        this.completed = true;
    }

    @Override
    public String toString() {
        return "Task [id=" + id + 
               ", title=" + title + 
               ", completed=" + completed + 
               (dueDate != null ? ", dueDate=" + dueDate : "") +
               "]";
    }
}