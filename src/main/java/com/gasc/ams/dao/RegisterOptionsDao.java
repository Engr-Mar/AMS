package com.gasc.ams.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.gasc.ams.config.DatabaseConfig;

@Repository
public class RegisterOptionsDao {
    public List<Map.Entry<String, String>> fetchOptions(String tableName) {
        List<Map.Entry<String, String>> results = new ArrayList<>();
        String query = "SELECT * FROM " + tableName +   " ORDER BY 2 ASC";

        try (Connection conn = DatabaseConfig.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            results.add(new AbstractMap.SimpleEntry<>("0", ""));    
            while (rs.next()) {
                String id = rs.getString(1);
                String name = rs.getString(2);

                if (name != null && !name.trim().isEmpty()) {
                    name = name.trim().toUpperCase();
                }

                results.add(new AbstractMap.SimpleEntry<>(id, name));
            }

            // priority values that should appear first
            List<String> priority = List.of("","確認不可", "無し");

            // sort using comparator
            results.sort((a, b) -> {
            String aVal = a.getValue();
            String bVal = b.getValue();

            boolean aPriority = aVal != null && priority.contains(aVal);
            boolean bPriority = bVal != null && priority.contains(bVal);

            if (aPriority && !bPriority) return -1;
            if (!aPriority && bPriority) return 1;

            // handle nulls safely
            if (aVal == null) return 1;
            if (bVal == null) return -1;

            return aVal.compareToIgnoreCase(bVal);
        });

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error fetching data from " + tableName, e);
        }

        return results;
    }

    public List<String> fetchOptionsCascade(String tableName, int columnIndex, String filter) {
        List<String> results = new ArrayList<>();
        String query = "SELECT * FROM " + tableName + filter + " ORDER BY " + columnIndex + " ASC";
        try (Connection conn = DatabaseConfig.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            String lastValue = null;
            while (rs.next()) {
                String name = rs.getString(columnIndex);

                if (name == null || name.trim().isEmpty() || name.equals(lastValue)) {
                    continue;
                }

                lastValue = name;
                results.add(name.toUpperCase());
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error fetching data from " + tableName, e);
        }

        return results;
    }

}
