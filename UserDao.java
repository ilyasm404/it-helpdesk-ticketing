package com.helpdesk.dao;

import com.helpdesk.db.DataAccessException;
import com.helpdesk.db.Database;
import com.helpdesk.model.Role;
import com.helpdesk.model.Technician;
import com.helpdesk.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** CRUD operations for users and technicians (stored in the same table). */
public class UserDao {

    public User create(User user) {
        String sql = "INSERT INTO users (name, email, role, specialty) VALUES (?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getRole().name());
            ps.setString(4, user.getSpecialty());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }
            return user;
        } catch (SQLException e) {
            throw new DataAccessException("Could not create user: " + e.getMessage(), e);
        }
    }

    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load user: " + e.getMessage(), e);
        }
    }

    public List<User> findAll() {
        return query("SELECT * FROM users ORDER BY id", null);
    }

    public List<User> findByRole(Role role) {
        return query("SELECT * FROM users WHERE role = ? ORDER BY id", role.name());
    }

    public boolean update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, specialty = ? WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getSpecialty());
            ps.setInt(4, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Could not update user: " + e.getMessage(), e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Foreign key RESTRICT fires if the user still owns tickets.
            throw new DataAccessException(
                    "Could not delete user (do they still have tickets?): " + e.getMessage(), e);
        }
    }

    private List<User> query(String sql, String param) {
        List<User> users = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (param != null) {
                ps.setString(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not list users: " + e.getMessage(), e);
        }
        return users;
    }

    private User map(ResultSet rs) throws SQLException {
        Role role = Role.valueOf(rs.getString("role"));
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        if (role == Role.TECHNICIAN) {
            return new Technician(id, name, email, rs.getString("specialty"));
        }
        return new User(id, name, email, role);
    }
}
