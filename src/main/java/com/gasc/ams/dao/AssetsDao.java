package com.gasc.ams.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.gasc.ams.config.DatabaseConfig;
import com.gasc.ams.dto.Asset;

@Repository
public class AssetsDao {
    public List<Asset> getAllAsset(String sort, String filter) {

        List<Asset> result = new ArrayList<>();

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
                " a.disposal_flag, " +
                " wr.flag_desc AS win10_status, " +
                " a.existcurrent, " +
                " a.updated_at " +
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
                "LEFT JOIN disposal_flag df ON a.disposal_status = df.flag_id ";

        if (filter != null && !filter.trim().isEmpty()) {
            if (!filter.equals(" WHERE 1=1 ")) {
                sql += filter;
            }
        }

        switch (sort) {
            case "N" -> sql += " ORDER BY a.asset_id DESC ";
            case "U" -> sql += " ORDER BY a.updated_at DESC NULLS LAST, a.status_change_date DESC NULLS LAST ";
            case "O" -> sql += " ORDER BY a.asset_id ASC ";
            default -> { }
        }

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()
        ) {

            while (rs.next()) {

                Asset dto = new Asset();

                dto.setId(rs.getString("asset_id"));
                dto.setCompName(rs.getString("computer_name"));
                // dto.setMacAddress(rs.getString("mac_address"));
                String mac = rs.getString("mac_address");
                dto.setMacAddress(mac != null ? mac : " - ");
                dto.setMaker(rs.getString("manufacturer_name"));
                dto.setModel(rs.getString("model_name"));
                dto.setOs(rs.getString("os_name"));
                dto.setOffice(rs.getString("office_software_name"));
                dto.setSecuritySoftware(rs.getString("security_software_name"));
                dto.setDomain(rs.getString("domain_name"));

                String cpuManufacturer = rs.getString("cpu_manufacturer");
                String cpuGeneration   = rs.getString("cpu_generation");
                String cpuModel        = rs.getString("cpu_model");

                dto.setCpu(
                    (cpuManufacturer != null ? cpuManufacturer : "-") + " " +
                    (cpuGeneration != null ? cpuGeneration : "-") + " " +
                    (cpuModel != null ? cpuModel : "-")
                );

                int ram = rs.getInt("ram_mb");
                dto.setRam(ram == 0 ? "-" : getMemoryConvertedString(String.valueOf(ram)));

                dto.setPcType(rs.getString("device_type_name"));

                String holding = rs.getString("holding_name");
                dto.setHolding((holding == null || holding.trim().isEmpty()) ? " - " : holding);

                java.sql.Date purchaseDate = rs.getDate("purchase_date");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                // dto.setPurchaseDate(purchaseDate != null ? purchaseDate.toString() : "");
                String formattedDate = "-";
                if (purchaseDate != null) {
                    LocalDate localDate = purchaseDate.toLocalDate();
                    formattedDate = localDate.format(formatter);
                }
                dto.setPurchaseDate(formattedDate);

                dto.setDisplayMaker(rs.getString("monitor_name"));
                dto.setKeyboard(rs.getString("has_keyboard"));
                dto.setMouse(rs.getString("has_mouse"));
                dto.setOther(rs.getString("other_peripherals"));

                dto.setLocation(rs.getString("branch"));
                dto.setFloor(rs.getString("floor_no"));
                dto.setRoom(rs.getString("room_name"));

                dto.setUser(rs.getString("employee_name"));
                dto.setProject(rs.getString("project_name"));

                String remarks = rs.getString("remarks");
                dto.setNote((remarks == null || remarks.trim().isEmpty()) ? " - " : remarks);

                dto.setStatus(rs.getString("disposal_flag"));
                dto.setStatusFilter(rs.getString("disposal_desc"));
                dto.setWin10Prio(rs.getString("win10_status"));
                dto.setExist2025(rs.getString("existcurrent"));

                result.add(dto);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load assets", e);
        }

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

}
