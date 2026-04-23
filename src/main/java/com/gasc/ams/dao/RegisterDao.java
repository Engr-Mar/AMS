package com.gasc.ams.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import org.springframework.stereotype.Repository;

import com.gasc.ams.config.DatabaseConfig;
import com.gasc.ams.dto.RegistrationForm;

@Repository
public class RegisterDao {

    public RegistrationForm getAssetById(int assetId) {

        String sql =
            "SELECT " +
            " a.asset_id, " +
            " a.computer_name, " +
            " a.mac_address, " +
            " m.manufacturer_name, " +
            " pm.model_name, " +
            " os.os_name, " +
            " off.office_software_name, " +
            " sec.security_software_name, " +
            " c.company_name AS domain_name, " +
            " cpu.cpu_manufacturer, " +
            " cpu.cpu_generation, " +
            " cpu.cpu_model, " +
            " a.ram_mb, " +
            " dt.device_type_name, " +
            " h.holding_name, " +
            " a.purchase_date, " +
            " mon.monitor_name, " +
            " a.has_keyboard, " +
            " a.has_mouse, " +
            " a.other_peripherals, " +
            " loc.branch, " +
            " loc.floor_no, " +
            " loc.room_name, " +
            " emp.employee_name, " +
            " pr.project_name, " +
            " a.remarks, " +
            " df.flag_desc AS disposal_desc, " +
            " a.disposal_status, " +
            " wr.flag_desc AS win10_status, " +
            " a.existcurrent " +
            "FROM pc_asset a " +
            "LEFT JOIN pc_model pm ON a.model_id = pm.model_id " +
            "LEFT JOIN manufacturer m ON pm.manufacturer_id = m.manufacturer_id " +
            "LEFT JOIN operating_system os ON a.os_id = os.os_id " +
            "LEFT JOIN office_software off ON a.office_id = off.office_id " +
            "LEFT JOIN security_software sec ON a.security_id = sec.security_id " +
            "LEFT JOIN company c ON a.domain = c.company_id " +
            "LEFT JOIN cpu cpu ON a.cpu_id = cpu.cpu_id " +
            "LEFT JOIN device_type dt ON a.device_type_id = dt.device_type_id " +
            "LEFT JOIN holding h ON a.holding = h.holding_id " +
            "LEFT JOIN monitor mon ON a.display_maker = mon.monitor_id " +
            "LEFT JOIN location loc ON a.location_id = loc.location_id " +
            "LEFT JOIN employee_user emp ON a.assigned_employee_id = emp.employee_id " +
            "LEFT JOIN win10update_flag wr ON a.win10_status = wr.flag_id " +
            "LEFT JOIN project pr ON a.project_id = pr.project_id " +
            "LEFT JOIN disposal_flag df ON a.disposal_status = df.flag_id " +
            "WHERE a.asset_id = ?";

        RegistrationForm dto = null;

        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, assetId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    dto = new RegistrationForm();

                    dto.setAssetId(rs.getInt("asset_id"));
                    dto.setComputerName(rs.getString("computer_name"));
                    dto.setMacAddress(rs.getString("mac_address"));
                    dto.setManufacturerName(rs.getString("manufacturer_name"));
                    dto.setModelNumber(rs.getString("model_name"));

                    dto.setOs(String.valueOf(fetchOrCreateID(
                            "operating_system",
                            "os_id",
                            "os_name",
                            rs.getString("os_name"))));

                    dto.setOffice(String.valueOf(fetchOrCreateID(
                            "office_software",
                            "office_id",
                            "office_software_name",
                            rs.getString("office_software_name"))));

                    dto.setSecuritySoftware(String.valueOf(fetchOrCreateID(
                            "security_software",
                            "security_id",
                            "security_software_name",
                            rs.getString("security_software_name"))));

                    dto.setDomain(rs.getString("domain_name"));

                    dto.setCpu1(rs.getString("cpu_manufacturer"));
                    dto.setCpu2(rs.getString("cpu_generation"));
                    dto.setCpu3(rs.getString("cpu_model"));

                    int ram = rs.getInt("ram_mb");
                    dto.setMemory(getMemoryConvertedString(ram != 0 ? String.valueOf(ram) : null));

                    dto.setPcTypes(rs.getString("device_type_name"));
                    dto.setOwned(rs.getString("holding_name"));

                    java.sql.Date purchaseDate = rs.getDate("purchase_date");
                    dto.setPurchaseDate(purchaseDate != null ? purchaseDate.toString() : "");

                    dto.setMonitor(rs.getString("monitor_name"));
                    dto.setKeyboard(rs.getInt("has_keyboard") == 1);
                    dto.setMouse(rs.getInt("has_mouse") == 1);
                    dto.setOthers(rs.getString("other_peripherals"));

                    dto.setLocation(rs.getString("branch"));
                    dto.setFloor(rs.getString("floor_no"));
                    dto.setRoom(rs.getString("room_name"));
                    dto.setCompUser(rs.getString("employee_name"));
                    dto.setCaseNumber(rs.getString("project_name"));

                    dto.setRemarks(rs.getString("remarks"));
                    dto.setDisposal(rs.getString("disposal_desc"));
                    dto.setDisposalFlag(rs.getInt("disposal_status") != 1);
                    dto.setWin10Priority(rs.getString("win10_status"));
                    dto.setExist2025(rs.getInt("existcurrent") == 1);
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return dto;
    }

    public String registerData(RegistrationForm registrationForm) {

        String sql = "INSERT INTO pc_asset (" +
                "computer_name, model_id, device_type_id, cpu_id, mac_address, ram_mb, " +
                "os_id, office_id, security_id, purchase_date, " +
                "display_maker, has_keyboard, has_mouse, other_peripherals, " +
                "location_id, assigned_employee_id, project_id, remarks, " +
                "domain, holding) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {

            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, registrationForm.getComputerName());

            pstmt.setInt(2, fetchOrCreatePCModelID(
                    fetchOrCreateManufacturerID(registrationForm.getManufacturerName()),
                    registrationForm.getModelNumber()));

            pstmt.setInt(3, fetchOrCreateID(
                    "device_type",
                    "device_type_id",
                    "device_type_name",
                    registrationForm.getPcTypes()));

            pstmt.setInt(4, fetchOrCreateCPUID(
                    registrationForm.getCpu1(),
                    registrationForm.getCpu2(),
                    registrationForm.getCpu3()));

            String mac = registrationForm.getMacAddress();

            if (mac == null || mac.isBlank()) {
                pstmt.setNull(5, java.sql.Types.VARCHAR);
            } else {
                pstmt.setString(5, mac);
            }

            pstmt.setInt(6, getMemoryConverted(registrationForm.getMemory()));

            pstmt.setInt(7, getIDorName(
                    "operating_system",
                    "os_id",
                    "os_name",
                    registrationForm.getOs()));

            pstmt.setInt(8, getIDorName(
                    "office_software",
                    "office_id",
                    "office_software_name",
                    registrationForm.getOffice()));

            pstmt.setInt(9, getIDorName(
                    "security_software",
                    "security_id",
                    "security_software_name",
                    registrationForm.getSecuritySoftware()));

            pstmt.setDate(10, convertToSqlDate(registrationForm.getPurchaseDate()));

            pstmt.setInt(11, fetchOrCreateID(
                    "monitor",
                    "monitor_id",
                    "monitor_name",
                    registrationForm.getMonitor()));

            pstmt.setInt(12, registrationForm.getKeyboard() != null && registrationForm.getKeyboard() ? 1 : 0);
            pstmt.setInt(13, registrationForm.getMouse() != null && registrationForm.getMouse() ? 1 : 0);

            pstmt.setString(14, registrationForm.getOthers());

            pstmt.setInt(15, fetchOrCreateLocationID(
                    registrationForm.getLocation(),
                    registrationForm.getFloor(),
                    registrationForm.getRoom()));

            pstmt.setInt(16, fetchOrCreateID(
                    "employee_user",
                    "employee_id",
                    "employee_name",
                    registrationForm.getCompUser()));

            pstmt.setInt(17, fetchOrCreateID(
                    "project",
                    "project_id",
                    "project_name",
                    registrationForm.getCaseNumber()));

            pstmt.setString(18, registrationForm.getRemarks());

            pstmt.setInt(19, fetchOrCreateID(
                    "company",
                    "company_id",
                    "company_name",
                    registrationForm.getDomain()));

            pstmt.setInt(20, fetchOrCreateID(
                    "holding",
                    "holding_id",
                    "holding_name",
                    registrationForm.getOwned()));

            int rowsAffected = pstmt.executeUpdate();

            conn.commit();

            // return rowsAffected > 0 ? "Data Registered Successfully" : "No data was inserted";
            return rowsAffected > 0 ? "データが正常に登録されました" : "データは挿入されませんでした";

        } catch (SQLException | ClassNotFoundException e) {

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }

            return "データの登録エラー:" + e.getMessage();

        } finally {

            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String updatePcAsset(RegistrationForm registrationForm) {

        String sql = "UPDATE pc_asset SET " +
                "computer_name = ?, model_id = ?, device_type_id = ?, cpu_id = ?, mac_address = ?, ram_mb = ?, " +
                "os_id = ?, office_id = ?, security_id = ?, purchase_date = ?, " +
                "display_maker = ?, has_keyboard = ?, has_mouse = ?, other_peripherals = ?, " +
                "location_id = ?, assigned_employee_id = ?, project_id = ?, " +
                "win10_status = ?, remarks = ?, domain = ?, holding = ?, " +
                "disposal_flag = ?, disposal_status = ?, existcurrent = ? " +
                "WHERE asset_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {

            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, registrationForm.getComputerName());

            pstmt.setInt(2, fetchOrCreatePCModelID(
                    fetchOrCreateManufacturerID(registrationForm.getManufacturerName()),
                    registrationForm.getModelNumber()));

            pstmt.setInt(3, fetchOrCreateID(
                    "device_type",
                    "device_type_id",
                    "device_type_name",
                    registrationForm.getPcTypes()));

            pstmt.setInt(4, fetchOrCreateCPUID(
                    registrationForm.getCpu1(),
                    registrationForm.getCpu2(),
                    registrationForm.getCpu3()));

            String mac = registrationForm.getMacAddress();

            if (mac == null || mac.isBlank()) {
                pstmt.setNull(5, java.sql.Types.VARCHAR);
            } else {
                pstmt.setString(5, mac);
            }

            pstmt.setInt(6, getMemoryConverted(registrationForm.getMemory()));

            pstmt.setInt(7, getIDorName(
                    "operating_system",
                    "os_id",
                    "os_name",
                    registrationForm.getOs()));

            pstmt.setInt(8, getIDorName(
                    "office_software",
                    "office_id",
                    "office_software_name",
                    registrationForm.getOffice()));

            pstmt.setInt(9, getIDorName(
                    "security_software",
                    "security_id",
                    "security_software_name",
                    registrationForm.getSecuritySoftware()));

            pstmt.setDate(10, convertToSqlDate(registrationForm.getPurchaseDate()));

            pstmt.setInt(11, fetchOrCreateID(
                    "monitor",
                    "monitor_id",
                    "monitor_name",
                    registrationForm.getMonitor()));

            pstmt.setInt(12, registrationForm.getKeyboard() != null && registrationForm.getKeyboard() ? 1 : 0);
            pstmt.setInt(13, registrationForm.getMouse() != null && registrationForm.getMouse() ? 1 : 0);

            pstmt.setString(14, registrationForm.getOthers());

            pstmt.setInt(15, fetchOrCreateLocationID(
                    registrationForm.getLocation(),
                    registrationForm.getFloor(),
                    registrationForm.getRoom()));

            pstmt.setInt(16, fetchOrCreateID(
                    "employee_user",
                    "employee_id",
                    "employee_name",
                    registrationForm.getCompUser()));

            pstmt.setInt(17, fetchOrCreateID(
                    "project",
                    "project_id",
                    "project_name",
                    registrationForm.getCaseNumber()));

            pstmt.setInt(18, fetchOrCreateID(
                    "win10update_flag",
                    "flag_id",
                    "flag_desc",
                    registrationForm.getWin10Priority()));

            pstmt.setString(19, registrationForm.getRemarks());

            pstmt.setInt(20, fetchOrCreateID(
                    "company",
                    "company_id",
                    "company_name",
                    registrationForm.getDomain()));

            pstmt.setInt(21, fetchOrCreateID(
                    "holding",
                    "holding_id",
                    "holding_name",
                    registrationForm.getOwned()));

            pstmt.setInt(22, registrationForm.getDisposalFlag() != null && registrationForm.getDisposalFlag() ? 1 : 0);

            pstmt.setInt(23, fetchOrCreateID(
                    "disposal_flag",
                    "flag_id",
                    "flag_desc",
                    registrationForm.getDisposal()));

            pstmt.setInt(24, registrationForm.getExist2025() != null && registrationForm.getExist2025() ? 1 : 0);

            pstmt.setInt(25, registrationForm.getAssetId());

            int rowsAffected = pstmt.executeUpdate();

            conn.commit();

            // return rowsAffected > 0 ? "Data Updated Successfully" : "No data was updated";

            return rowsAffected > 0 ? "データが正常に更新されました" : "データは更新されませんでした";

        } catch (SQLException | ClassNotFoundException e) {

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }

            return "データの更新中にエラーが発生しました: " + e.getMessage();

        } finally {

            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Integer fetchOrCreateCPUID(String manufacturer, String generation, String model) {

        Integer cpuId = null;

        StringBuilder selectSQL = new StringBuilder(
            "SELECT cpu_id FROM cpu WHERE UPPER(TRIM(cpu_manufacturer)) = UPPER(TRIM(?)) "
        );

        boolean hasGeneration = generation != null && !generation.trim().isEmpty();
        boolean hasModel = model != null && !model.trim().isEmpty();

        if (hasGeneration) {
            selectSQL.append("AND UPPER(TRIM(cpu_generation)) = UPPER(TRIM(?)) ");
        } else {
            selectSQL.append("AND cpu_generation IS NULL ");
        }

        if (hasModel) {
            selectSQL.append("AND UPPER(TRIM(cpu_model)) = UPPER(TRIM(?)) ");
        } else {
            selectSQL.append("AND cpu_model IS NULL ");
        }

        String insertSQL =
            "INSERT INTO cpu (cpu_manufacturer, cpu_generation, cpu_model) VALUES (?, ?, ?)";

        Connection conn = null;

        try {

            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            /* ------------------- CHECK IF CPU EXISTS ------------------- */

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL.toString())) {

                int index = 1;

                selectStmt.setString(index++, manufacturer);

                if (hasGeneration) {
                    selectStmt.setString(index++, generation);
                }

                if (hasModel) {
                    selectStmt.setString(index++, model);
                }

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        conn.commit();
                        return rs.getInt("cpu_id");
                    }
                }
            }

            /* ------------------- INSERT NEW CPU ------------------- */

            try (PreparedStatement insertStmt =
                    conn.prepareStatement(insertSQL, new String[]{"cpu_id"})) {

                insertStmt.setString(1, manufacturer);

                if (hasGeneration) {
                    insertStmt.setString(2, generation);
                } else {
                    insertStmt.setNull(2, java.sql.Types.VARCHAR);
                }

                if (hasModel) {
                    insertStmt.setString(3, model);
                } else {
                    insertStmt.setNull(3, java.sql.Types.VARCHAR);
                }

                insertStmt.executeUpdate();

                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {

                    if (generatedKeys.next()) {
                        cpuId = generatedKeys.getInt(1);
                    }

                }
            }

            conn.commit();

        } catch (SQLException | ClassNotFoundException e) {

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new RuntimeException("Rollback failed", rollbackEx);
                }
            }

            throw new RuntimeException("Error fetching or creating cpu_id", e);

        } finally {

            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }

        return cpuId;
    }

    public Integer fetchOrCreateLocationID(String branch, String floorNo, String roomName) {

        Integer locationID = null;

        // Normalize inputs
        branch = (branch == null || branch.trim().isEmpty()) ? null : branch.trim();
        floorNo = (floorNo == null || floorNo.trim().isEmpty()) ? null : floorNo.trim();
        roomName = (roomName == null || roomName.trim().isEmpty()) ? null : roomName.trim();

        String selectSQL =
            "SELECT location_id FROM location " +
            "WHERE (branch IS NOT DISTINCT FROM ?) " +
            "AND (floor_no IS NOT DISTINCT FROM ?) " +
            "AND (room_name IS NOT DISTINCT FROM ?)";

        String insertSQL =
            "INSERT INTO location (branch, floor_no, room_name) " +
            "VALUES (?, ?, ?) RETURNING location_id";

        try (Connection conn = DatabaseConfig.getConnection()) {

            conn.setAutoCommit(false);

            /* ---------- CHECK EXISTING ---------- */

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {

                selectStmt.setObject(1, branch);
                selectStmt.setObject(2, floorNo);
                selectStmt.setObject(3, roomName);

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        conn.commit();
                        return rs.getInt("location_id");
                    }
                }
            }

            /* ---------- INSERT NEW ---------- */

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {

                insertStmt.setObject(1, branch);
                insertStmt.setObject(2, floorNo);
                insertStmt.setObject(3, roomName);

                try (ResultSet rs = insertStmt.executeQuery()) {
                    if (rs.next()) {
                        locationID = rs.getInt(1);
                    }
                }
            }

            conn.commit();

        } catch (SQLException | ClassNotFoundException ex) {
            throw new RuntimeException("Error fetching or creating location_id", ex);
        }

        return locationID;
    }

    public Integer fetchOrCreateManufacturerID(String manufacturer) {

        // Default value rule (your original logic)
        if (manufacturer == null || manufacturer.trim().isEmpty()) {
            return 1;
        }

        manufacturer = manufacturer.trim();
        Integer manufacturerID = null;

        String selectSQL =
            "SELECT manufacturer_id FROM manufacturer " +
            "WHERE UPPER(TRIM(manufacturer_name)) = UPPER(TRIM(?))";

        String insertSQL =
            "INSERT INTO manufacturer (manufacturer_name) VALUES (?) RETURNING manufacturer_id";

        try (Connection conn = DatabaseConfig.getConnection()) {

            conn.setAutoCommit(false);

            /* ---------- CHECK EXISTING ---------- */

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {

                selectStmt.setString(1, manufacturer);

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        conn.commit();
                        return rs.getInt("manufacturer_id");
                    }
                }
            }

            /* ---------- INSERT NEW ---------- */

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {

                insertStmt.setString(1, manufacturer);

                try (ResultSet rs = insertStmt.executeQuery()) {
                    if (rs.next()) {
                        manufacturerID = rs.getInt(1);
                    }
                }
            }

            conn.commit();

        } catch (SQLException | ClassNotFoundException ex) {
            throw new RuntimeException("Error fetching or creating manufacturer_id", ex);
        }

        return manufacturerID;
    }

    public Integer fetchOrCreatePCModelID(int manufacturer, String value) {

        Integer modelId = null;

        // Default value rule
        String modelName = (value == null || value.trim().isEmpty())
                ? "確認不可"
                : value.trim();

        String selectSQL =
                "SELECT model_id FROM pc_model " +
                "WHERE manufacturer_id = ? AND model_name = ?";

        String insertSQL =
                "INSERT INTO pc_model (manufacturer_id, model_name) " +
                "VALUES (?, ?) RETURNING model_id";

        try (Connection conn = DatabaseConfig.getConnection()) {

            conn.setAutoCommit(false);

            /* ---------- CHECK EXISTING ---------- */

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {

                selectStmt.setInt(1, manufacturer);
                selectStmt.setString(2, modelName);

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        conn.commit();
                        return rs.getInt("model_id");
                    }
                }
            }

            /* ---------- INSERT NEW ---------- */

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {

                insertStmt.setInt(1, manufacturer);
                insertStmt.setString(2, modelName);

                try (ResultSet rs = insertStmt.executeQuery()) {
                    if (rs.next()) {
                        modelId = rs.getInt(1);
                    }
                }
            }

            conn.commit();

        } catch (SQLException | ClassNotFoundException ex) {
            throw new RuntimeException("Error fetching or creating pc_model_id", ex);
        }

        return modelId;
    }

    public Integer fetchOrCreateID(String table, String columnId, String column, String value) {

        if (value == null || value.trim().isEmpty()) {
            return 1;
        }

        String trimmedValue = value.trim();
        Integer id = null;

        String selectSQL =
                "SELECT " + columnId +
                " FROM " + table +
                " WHERE UPPER(TRIM(" + column + ")) = UPPER(TRIM(?))";

        String insertSQL =
                "INSERT INTO " + table +
                " (" + column + ") VALUES (?) RETURNING " + columnId;

        try (Connection conn = DatabaseConfig.getConnection()) {

            conn.setAutoCommit(false);

            /* ---------- 1️⃣ CHECK EXISTING ---------- */

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {

                selectStmt.setString(1, trimmedValue);

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        conn.commit();
                        return rs.getInt(columnId);
                    }
                }
            }

            /* ---------- 2️⃣ INSERT NEW ---------- */

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {

                insertStmt.setString(1, trimmedValue);

                try (ResultSet rs = insertStmt.executeQuery()) {
                    if (rs.next()) {
                        id = rs.getInt(1);
                    }
                }
            }

            conn.commit();

        } catch (SQLException | ClassNotFoundException ex) {
            throw new RuntimeException("Error fetching or creating ID for table: " + table, ex);
        }

        return id;
    }

    public Integer getMemoryConverted(String memory) {
        int gByte = 1024;
        int tByte = gByte * gByte;

        if (memory == null || memory.trim().isEmpty()) {
            return 0;
        }

        String upper = memory.toUpperCase();
        String numeric = upper.replaceAll("\\D", "");

        if (numeric.isEmpty()) {
            return 0;
        }

        int value = Integer.parseInt(numeric);
        int result;

        if (upper.contains("TB")) {
            result = (int) (value * tByte);
            memory = value + "TB";
        } else if (upper.contains("GB")) {
            result = value * gByte;
            memory = value + "GB";
        } else if (upper.contains("MB")) {
            result = value;
            memory = value + "MB";
        } else {
            result = value;
            memory = value + "MB";
        }
        
        fetchOrCreateID("MEMORYRAM","MEMORY_ID","MEMORY_SPEC", memory);
        
        return result;
    }

    public String getMemoryConvertedString(String memory) {

        if (memory == null || memory.trim().isEmpty()) {
            return "";
        }

        String numeric = memory.replaceAll("\\D", "");
        if (numeric.isEmpty()) {
            return "";
        }

        long valueMb = Long.parseLong(numeric);

        final long MB_IN_GB = 1024;
        final long MB_IN_TB = MB_IN_GB * 1024;

        DecimalFormat df = new DecimalFormat("#.#"); 

        if (valueMb >= MB_IN_TB) {
            double tb = (double) valueMb / MB_IN_TB;
            return df.format(tb) + "TB";

        } else if (valueMb >= MB_IN_GB) {
            double gb = (double) valueMb / MB_IN_GB;
            return df.format(gb) + "GB";

        } else {
            return valueMb + "MB";
        }
    }

    public void print(RegistrationForm form) {
        System.out.println("=== Processed Registration Data Preview ===");

        System.out.println("Computer Name: " + form.getComputerName());
        System.out.println("Manufacturer: " + form.getManufacturerName());
        System.out.println("Model Number: " + form.getModelNumber());
        System.out.println("Device Type: " + form.getPcTypes());
        System.out.println("CPU: " + ((form.getCpu1() != null ? form.getCpu1() : "")
                + (form.getCpu2() != null ? " / " + form.getCpu2() : "")
                + (form.getCpu3() != null ? " / " + form.getCpu3() : "")));
        System.out.println("MAC Address: " + form.getMacAddress());
        System.out.println("RAM: " + (form.getMemory() != null ? form.getMemory() : "0"));
        System.out.println("OS: " + form.getOs());
        System.out.println("Office Software: " + form.getOffice());
        System.out.println("Security Software: " + form.getSecuritySoftware());
        System.out.println("Purchase Date: " + form.getPurchaseDate());
        System.out.println("Monitor: " + form.getMonitor());
        System.out.println("Has Keyboard: " + (form.getKeyboard() != null && form.getKeyboard() ? "Yes" : "No"));
        System.out.println("Has Mouse: " + (form.getMouse() != null && form.getMouse() ? "Yes" : "No"));
        System.out.println("Other Peripherals: " + form.getOthers());
        System.out.println("Location: " + ((form.getLocation() != null ? form.getLocation() : "")
                + (form.getFloor() != null ? " / Floor " + form.getFloor() : "")
                + (form.getRoom() != null ? " / Room " + form.getRoom() : "")));
        System.out.println("Assigned Employee: " + form.getCompUser());
        System.out.println("Case Number: " + form.getCaseNumber());
        System.out.println("Win10 Priority: " + (form.getWin10Priority() != null ? form.getWin10Priority() : "0"));
        System.out.println("Remarks: " + form.getRemarks());
        System.out.println("Disposal Flag: " + (form.getDisposalFlag() != null && form.getDisposalFlag() ? "Yes" : "No"));

        System.out.println("================================");
    }

    public static java.sql.Date convertToSqlDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null; 
        }

        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date utilDate = sdf.parse(dateStr);
            return new java.sql.Date(utilDate.getTime());
        } catch (java.text.ParseException e) {
            System.err.println("Invalid date format: " + dateStr + ". Expected yyyy-MM-dd.");
            return null;
        }
    }

    public Integer getIDorName(String table, String columnId, String column, String value ){
        
        Integer id = 1;

        if (value == null || value.trim().isEmpty()) {
            return id;
        }
        
        try {

            id = Integer.valueOf(value.trim()); 

        } catch (NumberFormatException e) {
            
            id = fetchOrCreateID( table, columnId, column, value);
        }

        return id;
    }
}
