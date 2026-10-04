package com.helpdesk;

import com.helpdesk.dao.TicketDao;
import com.helpdesk.dao.TicketFilter;
import com.helpdesk.dao.UserDao;
import com.helpdesk.db.Database;
import com.helpdesk.model.Priority;
import com.helpdesk.model.Role;
import com.helpdesk.model.Status;
import com.helpdesk.model.Technician;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import com.helpdesk.service.TicketService;

import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Console front end for the IT Help Desk Ticketing System.
 *
 * @author Ilyas Mohamed
 */
public class Main {
    private static final Scanner IN = new Scanner(System.in);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final UserDao USERS = new UserDao();
    private static final TicketService TICKETS = new TicketService(new TicketDao(), USERS);

    public static void main(String[] args) {
        try (Connection ignored = Database.getConnection()) {
            System.out.println("Connected to database.");
        } catch (Exception e) {
            System.err.println("Cannot connect to the database: " + e.getMessage());
            System.err.println("Check db.properties and that MySQL is running.");
            return;
        }

        boolean running = true;
        while (running) {
            System.out.println("\n=== IT Help Desk ===");
            System.out.println("1) Tickets");
            System.out.println("2) Users & technicians");
            System.out.println("0) Exit");
            switch (readLine("Choose: ")) {
                case "1" -> ticketMenu();
                case "2" -> userMenu();
                case "0" -> running = false;
                default -> System.out.println("Invalid choice.");
            }
        }
        System.out.println("Goodbye.");
    }

    // ------------------------------------------------------------------ tickets

    private static void ticketMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Tickets ---");
            System.out.println("1) Create ticket");
            System.out.println("2) List all tickets");
            System.out.println("3) View ticket details");
            System.out.println("4) Filter tickets");
            System.out.println("5) Update status");
            System.out.println("6) Change priority");
            System.out.println("7) Assign to technician");
            System.out.println("8) Unassign");
            System.out.println("9) Edit title/description");
            System.out.println("10) Delete ticket");
            System.out.println("0) Back");
            String choice = readLine("Choose: ");
            try {
                switch (choice) {
                    case "1" -> createTicket();
                    case "2" -> printTickets(TICKETS.listAll());
                    case "3" -> printDetails(TICKETS.getTicket(readInt("Ticket id: ")));
                    case "4" -> filterTickets();
                    case "5" -> {
                        int id = readInt("Ticket id: ");
                        Status s = readEnum(Status.class, "New status", false);
                        System.out.println("Updated: " + TICKETS.updateStatus(id, s));
                    }
                    case "6" -> {
                        int id = readInt("Ticket id: ");
                        Priority p = readEnum(Priority.class, "New priority", false);
                        System.out.println("Updated: " + TICKETS.updatePriority(id, p));
                    }
                    case "7" -> assignTicket();
                    case "8" -> System.out.println("Updated: " + TICKETS.unassign(readInt("Ticket id: ")));
                    case "9" -> {
                        int id = readInt("Ticket id: ");
                        String title = readLine("New title (blank = keep): ");
                        String desc = readLine("New description (blank = keep): ");
                        System.out.println("Updated: " + TICKETS.editDetails(id, title, desc));
                    }
                    case "10" -> {
                        int id = readInt("Ticket id: ");
                        System.out.println(TICKETS.delete(id) ? "Deleted." : "No such ticket.");
                    }
                    case "0" -> back = true;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void createTicket() {
        int userId = readInt("Your user id: ");
        String title = readLine("Title: ");
        String desc = readLine("Description: ");
        Priority p = readEnum(Priority.class, "Priority", false);
        Ticket t = TICKETS.createTicket(title, desc, p, userId);
        System.out.println("Created: " + t);
    }

    private static void assignTicket() {
        System.out.println("Technicians:");
        USERS.findByRole(Role.TECHNICIAN).forEach(u -> System.out.println("  " + u));
        int ticketId = readInt("Ticket id: ");
        int techId = readInt("Technician id: ");
        System.out.println("Updated: " + TICKETS.assign(ticketId, techId));
    }

    private static void filterTickets() {
        System.out.println("Leave a field blank to ignore it.");
        TicketFilter f = new TicketFilter()
                .status(readEnum(Status.class, "Status", true))
                .priority(readEnum(Priority.class, "Priority", true));
        String assigned = readLine("Assigned technician id (blank = any, 'none' = unassigned): ");
        if (assigned.equalsIgnoreCase("none")) {
            f.unassignedOnly(true);
        } else if (!assigned.isBlank()) {
            try {
                f.assignedTo(Integer.parseInt(assigned));
            } catch (NumberFormatException e) {
                System.out.println("Ignoring invalid technician id.");
            }
        }
        f.keyword(readLine("Keyword in title/description: "));
        printTickets(TICKETS.filter(f));
    }

    private static void printTickets(List<Ticket> tickets) {
        if (tickets.isEmpty()) {
            System.out.println("No tickets found.");
            return;
        }
        System.out.printf("%-5s %-9s %-12s %-9s %-9s %s%n", "ID", "PRIORITY", "STATUS", "CREATOR", "ASSIGNEE", "TITLE");
        for (Ticket t : tickets) {
            System.out.printf("%-5d %-9s %-12s %-9d %-9s %s%n",
                    t.getId(), t.getPriority(), t.getStatus(), t.getCreatedBy(),
                    t.getAssignedTo() == null ? "-" : t.getAssignedTo().toString(), t.getTitle());
        }
        System.out.println(tickets.size() + " ticket(s).");
    }

    private static void printDetails(Ticket t) {
        String creator = USERS.findById(t.getCreatedBy()).map(User::toString).orElse("(deleted user)");
        String assignee = t.getAssignedTo() == null
                ? "Unassigned"
                : USERS.findById(t.getAssignedTo()).map(User::toString).orElse("(deleted user)");
        System.out.println("Ticket #" + t.getId() + ": " + t.getTitle());
        System.out.println("  Status:      " + t.getStatus());
        System.out.println("  Priority:    " + t.getPriority());
        System.out.println("  Created by:  " + creator);
        System.out.println("  Assigned to: " + assignee);
        System.out.println("  Created:     " + t.getCreatedAt().format(FMT));
        System.out.println("  Updated:     " + t.getUpdatedAt().format(FMT));
        System.out.println("  Description: " + t.getDescription());
    }

    // -------------------------------------------------------------------- users

    private static void userMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Users & technicians ---");
            System.out.println("1) Add end user");
            System.out.println("2) Add technician");
            System.out.println("3) List all");
            System.out.println("4) List technicians");
            System.out.println("5) Update user");
            System.out.println("6) Delete user");
            System.out.println("0) Back");
            String choice = readLine("Choose: ");
            try {
                switch (choice) {
                    case "1" -> {
                        User u = USERS.create(new User(readLine("Name: "), readLine("Email: ")));
                        System.out.println("Created: " + u);
                    }
                    case "2" -> {
                        Technician t = new Technician(readLine("Name: "), readLine("Email: "),
                                readLine("Specialty: "));
                        System.out.println("Created: " + USERS.create(t));
                    }
                    case "3" -> USERS.findAll().forEach(System.out::println);
                    case "4" -> USERS.findByRole(Role.TECHNICIAN).forEach(System.out::println);
                    case "5" -> updateUser();
                    case "6" -> {
                        int id = readInt("User id: ");
                        System.out.println(USERS.delete(id) ? "Deleted." : "No such user.");
                    }
                    case "0" -> back = true;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void updateUser() {
        int id = readInt("User id: ");
        User u = USERS.findById(id).orElse(null);
        if (u == null) {
            System.out.println("No such user.");
            return;
        }
        String name = readLine("Name [" + u.getName() + "]: ");
        String email = readLine("Email [" + u.getEmail() + "]: ");
        if (!name.isBlank()) u.setName(name);
        if (!email.isBlank()) u.setEmail(email);
        if (u instanceof Technician t) {
            String specialty = readLine("Specialty [" + t.getSpecialty() + "]: ");
            if (!specialty.isBlank()) t.setSpecialty(specialty);
        }
        USERS.update(u);
        System.out.println("Updated: " + u);
    }

    // ------------------------------------------------------------------ helpers

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return IN.hasNextLine() ? IN.nextLine().trim() : "0";
    }

    private static int readInt(String prompt) {
        while (true) {
            String s = readLine(prompt);
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    private static <E extends Enum<E>> E readEnum(Class<E> type, String label, boolean optional) {
        while (true) {
            String options = Arrays.toString(type.getEnumConstants());
            String s = readLine(label + " " + options + (optional ? " (blank = any)" : "") + ": ")
                    .toUpperCase().replace(' ', '_');
            if (s.isEmpty() && optional) {
                return null;
            }
            try {
                return Enum.valueOf(type, s);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid value.");
            }
        }
    }
}
