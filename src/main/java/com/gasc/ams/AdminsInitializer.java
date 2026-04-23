package com.gasc.ams;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gasc.ams.config.DatabaseConfig;


//@Component 
public class AdminsInitializer implements CommandLineRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) {
        String encoded = passwordEncoder.encode("password");
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement del = conn.prepareStatement("TRUNCATE TABLE admins RESTART IDENTITY");
             PreparedStatement ins = conn.prepareStatement("INSERT INTO admins (username, password, role) VALUES (?, ?, ?)") ) {

            del.executeUpdate();

            ins.setString(1, "admin");
            ins.setString(2, encoded);
            ins.setString(3, "ROLE_ADMIN");
            ins.executeUpdate();

            System.out.println("ADMINS table reset and admin user inserted.");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Failed to initialize ADMINS table: " + e.getMessage());
        }
    }
}
