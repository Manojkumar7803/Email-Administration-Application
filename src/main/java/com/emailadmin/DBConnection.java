
package com.emailadmin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private static final String url =
            "jdbc:postgresql://localhost:5432/Email";

    private static final String user = "postgres";

    private static final String password = "123";

    private DBConnection() {
        // Prevent object creation
    }

    public static Connection getConnection() throws SQLException {

        try {
            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver not found", e);
        }

        return DriverManager.getConnection(url, user, password);
    }
}
