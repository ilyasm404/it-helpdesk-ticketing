package com.helpdesk.model;

/** A support technician who can be assigned tickets. */
public class Technician extends User {
    private String specialty;

    public Technician(int id, String name, String email, String specialty) {
        super(id, name, email, Role.TECHNICIAN);
        this.specialty = specialty;
    }

    /** Convenience constructor for a new (not yet persisted) technician. */
    public Technician(String name, String email, String specialty) {
        this(0, name, email, specialty);
    }

    @Override
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    @Override
    public String toString() {
        return super.toString() + (specialty == null || specialty.isBlank() ? "" : " - " + specialty);
    }
}
