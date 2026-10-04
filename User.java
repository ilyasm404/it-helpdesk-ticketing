package com.helpdesk.model;

/** A person who can submit tickets. Technicians are a specialised kind of user. */
public class User {
    private int id;
    private String name;
    private String email;
    private final Role role;

    public User(int id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    /** Convenience constructor for a new (not yet persisted) end user. */
    public User(String name, String email) {
        this(0, name, email, Role.END_USER);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }

    /** Overridden by Technician; end users have no specialty. */
    public String getSpecialty() { return null; }

    @Override
    public String toString() {
        return String.format("#%d %s <%s> [%s]", id, name, email, role);
    }
}
