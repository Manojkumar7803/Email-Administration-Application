package com.emailadmin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class EmployeeDAO {

    public void create(String name, String department, String email,
                       String plainPassword, String alternateEmail, int capacityMb)
            throws SQLException {
        String sql = "INSERT INTO employees "
                + "(employee_name, department, email_address, password_hash, "
                + "alternate_email, mailbox_capacity_mb) VALUES (?, ?, ?, ?, ?, ?)";
        String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, department);
            ps.setString(3, email);
            ps.setString(4, hash);
            ps.setString(5, blankToNull(alternateEmail));
            ps.setInt(6, capacityMb);
            ps.executeUpdate();
        }
    }

    public List<Employee> findAll() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id, employee_name, department, email_address, "
                + "alternate_email, mailbox_capacity_mb FROM employees ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                employees.add(new Employee(
                        rs.getInt("id"), rs.getString("employee_name"),
                        rs.getString("department"), rs.getString("email_address"),
                        rs.getString("alternate_email"), rs.getInt("mailbox_capacity_mb")));
            }
        }
        return employees;
    }

    public void updateDetails(int id, String alternateEmail, int capacityMb)
            throws SQLException {
        String sql = "UPDATE employees SET alternate_email=?, mailbox_capacity_mb=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, blankToNull(alternateEmail));
            ps.setInt(2, capacityMb);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM employees WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
