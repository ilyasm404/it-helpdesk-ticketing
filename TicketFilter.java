package com.helpdesk.dao;

import com.helpdesk.model.Priority;
import com.helpdesk.model.Status;

/** Optional criteria for searching tickets. Any field left null is ignored. */
public class TicketFilter {
    private Status status;
    private Priority priority;
    private Integer assignedTo;
    private Integer createdBy;
    private boolean unassignedOnly;
    private String keyword;

    public TicketFilter status(Status status) { this.status = status; return this; }
    public TicketFilter priority(Priority priority) { this.priority = priority; return this; }
    public TicketFilter assignedTo(Integer technicianId) { this.assignedTo = technicianId; return this; }
    public TicketFilter createdBy(Integer userId) { this.createdBy = userId; return this; }
    public TicketFilter unassignedOnly(boolean value) { this.unassignedOnly = value; return this; }
    public TicketFilter keyword(String keyword) { this.keyword = keyword; return this; }

    public Status getStatus() { return status; }
    public Priority getPriority() { return priority; }
    public Integer getAssignedTo() { return assignedTo; }
    public Integer getCreatedBy() { return createdBy; }
    public boolean isUnassignedOnly() { return unassignedOnly; }
    public String getKeyword() { return keyword; }
}
