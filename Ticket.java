package com.helpdesk.model;

import java.time.LocalDateTime;

public class Ticket {
    private int id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private int createdBy;
    private Integer assignedTo;          // null = unassigned
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Ticket(int id, String title, String description, Priority priority, Status status,
                  int createdBy, Integer assignedTo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.createdBy = createdBy;
        this.assignedTo = assignedTo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** Creates a new (not yet persisted) ticket: OPEN and unassigned. */
    public Ticket(String title, String description, Priority priority, int createdBy) {
        this(0, title, description, priority, Status.OPEN, createdBy, null, null, null);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public int getCreatedBy() { return createdBy; }
    public Integer getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Integer assignedTo) { this.assignedTo = assignedTo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public String toString() {
        return String.format("Ticket #%d [%s/%s] %s", id, priority, status, title);
    }
}
