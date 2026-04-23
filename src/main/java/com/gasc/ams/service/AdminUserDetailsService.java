package com.gasc.ams.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.gasc.ams.config.DatabaseConfig;

public class AdminUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by username: {}", username);
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM ADMINS WHERE USERNAME = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    logger.warn("User not found in ADMINS table: {}", username);
                    throw new UsernameNotFoundException("User not found: " + username);
                }

                String pwd = null;
                try {
                    pwd = rs.getString("PASSWORD");
                } catch (SQLException ignored) {
                }
                if (pwd == null) {
                    try { pwd = rs.getString("PASS"); } catch (SQLException ignored) {}
                    try { if (pwd == null) pwd = rs.getString("PWD"); } catch (SQLException ignored) {}
                }

                String role = null;
                try { role = rs.getString("ROLE"); } catch (SQLException ignored) {}
                if (role == null) role = "ROLE_USER";

                Collection<GrantedAuthority> auths = new ArrayList<>();
                auths.add(new SimpleGrantedAuthority(role));

                logger.debug("Loaded user {} with role {} (password present={})", username, role, pwd != null);

                return new User(username, pwd == null ? "" : pwd, auths);
            }
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("Error loading user {} from ADMINS: {}", username, e.getMessage(), e);
            throw new UsernameNotFoundException("Error loading user: " + e.getMessage(), e);
        }
    }


}
