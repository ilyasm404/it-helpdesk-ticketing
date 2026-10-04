package com.helpdesk.service;

import com.helpdesk.dao.TicketDao;
import com.helpdesk.dao.TicketFilter;
import com.helpdesk.dao.UserDao;
import com.helpdesk.model.Priority;
import com.helpdesk.model.Role;
import com.helpdesk.model.Status;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;

import java.util.List;

/** Business rules for tickets, sitting between the console UI and the DAOs. */
public class TicketService {
    private final TicketDao ticketDao;
    private final UserDao userDao;

    public TicketService(TicketDao ticketDao, UserDao userDao) {
        this.ticketDao = ticketDao;
        this.userDao = userDao;
    }

    public Ticket createTicket(String title, String description, Priority priority, int createdBy) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description is required.");
        }
        userDao.findById(createdBy)
                .orElseThrow(() -> new IllegalArgumentException("No user with id " + createdBy));
        return ticketDao.create(new Ticket(title.trim(), description.trim(), priority, createdBy));
    }

    public Ticket getTicket(int id) {
        return ticketDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No ticket with id " + id));
    }

    public List<Ticket> listAll() {
        return ticketDao.findAll();
    }

    public List<Ticket> filter(TicketFilter filter) {
        return ticketDao.search(filter);
    }

    public Ticket updateStatus(int ticketId, Status newStatus) {
        Ticket t = getTicket(ticketId);
        if (t.getStatus() == Status.CLOSED && newStatus != Status.OPEN) {
            throw new IllegalStateException("A closed ticket can only be reopened (set to OPEN).");
        }
        t.setStatus(newStatus);
        ticketDao.update(t);
        return getTicket(ticketId);
    }

    public Ticket updatePriority(int ticketId, Priority newPriority) {
        Ticket t = getTicket(ticketId);
        t.setPriority(newPriority);
        ticketDao.update(t);
        return getTicket(ticketId);
    }

    /** Assigns a ticket to a technician; an OPEN ticket moves to IN_PROGRESS automatically. */
    public Ticket assign(int ticketId, int technicianId) {
        Ticket t = getTicket(ticketId);
        User tech = userDao.findById(technicianId)
                .orElseThrow(() -> new IllegalArgumentException("No user with id " + technicianId));
        if (tech.getRole() != Role.TECHNICIAN) {
            throw new IllegalArgumentException(tech.getName() + " is not a technician.");
        }
        t.setAssignedTo(technicianId);
        if (t.getStatus() == Status.OPEN) {
            t.setStatus(Status.IN_PROGRESS);
        }
        ticketDao.update(t);
        return getTicket(ticketId);
    }

    public Ticket unassign(int ticketId) {
        Ticket t = getTicket(ticketId);
        t.setAssignedTo(null);
        ticketDao.update(t);
        return getTicket(ticketId);
    }

    public Ticket editDetails(int ticketId, String title, String description) {
        Ticket t = getTicket(ticketId);
        if (title != null && !title.isBlank()) {
            t.setTitle(title.trim());
        }
        if (description != null && !description.isBlank()) {
            t.setDescription(description.trim());
        }
        ticketDao.update(t);
        return getTicket(ticketId);
    }

    public boolean delete(int ticketId) {
        return ticketDao.delete(ticketId);
    }
}
