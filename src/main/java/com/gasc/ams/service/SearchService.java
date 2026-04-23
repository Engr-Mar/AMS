package com.gasc.ams.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gasc.ams.dao.AssetsDao;
import com.gasc.ams.dto.Asset;
import com.gasc.ams.dto.SearchForm;

@Service
public class SearchService {

	@Autowired
	AssetsDao assetsDao;

	public List<Asset> fetchAssets(String sort, String filter) {
		List<Asset> result;
		result = assetsDao.getAllAsset(sort, filter);
		return result;
	}

	public void trySet(java.util.function.Consumer<String> setter, ResultSet rs, String... cols) {
		for (String c : cols) {
			try {
				String v = rs.getString(c);
				if (v != null) {
					setter.accept(v);
					return;
				}
			} catch (SQLException ignored) {
			}
		}
	}

	public String getSearchFilter(SearchForm form) {

		StringBuilder filter = new StringBuilder();

		List<String> deviceTypes = new ArrayList<>();
		List<String> ramConditions = new ArrayList<>();
		List<String> domainConditions = new ArrayList<>();
		List<String> locationConditions = new ArrayList<>();
		List<String> winUpConditions = new ArrayList<>();

		filter.append(" WHERE 1=1 ");

		if (form.getCompName() != null && !form.getCompName().trim().isEmpty()) {
			if (Boolean.TRUE.equals(form.getCompNameExact())) {
				filter.append(" AND a.computer_name = '").append(form.getCompName()).append("'");
			} else {
				filter.append(" AND UPPER(a.computer_name) LIKE UPPER('%")
						.append(form.getCompName().toUpperCase()).append("%')");
			}
		}

		if (form.getDesktop()) deviceTypes.add("デスクトップ");
		if (form.getLaptop()) deviceTypes.add("ノート");
		if (form.getServer()) deviceTypes.add("サーバ");
		if (form.getOther()) deviceTypes.add("その他");

		if (!deviceTypes.isEmpty()) {
			filter.append(" AND (");
			for (int i = 0; i < deviceTypes.size(); i++) {
				if (i > 0) filter.append(" OR ");
				filter.append("dt.device_type_name = '").append(deviceTypes.get(i)).append("'");
			}
			filter.append(")");
		}

		if (form.getManufacturer() != null && !form.getManufacturer().trim().isEmpty()) {
			filter.append(" AND UPPER(m.manufacturer_name) = UPPER('")
					.append(form.getManufacturer()).append("')");
		}

		if (form.getModel() != null && !form.getModel().trim().isEmpty()) {
			if (Boolean.TRUE.equals(form.getModelExact())) {
				filter.append(" AND UPPER(pm.model_name) = UPPER('")
						.append(form.getModel()).append("')");
			} else {
				filter.append(" AND UPPER(pm.model_name) LIKE UPPER('%")
						.append(form.getModel().toUpperCase()).append("%')");
			}
		}

		if (form.getMacAddress() != null && !form.getMacAddress().trim().isEmpty()) {
			if (Boolean.TRUE.equals(form.getMacAddressExact())) {
				filter.append(" AND a.mac_address = '").append(form.getMacAddress()).append("'");
			} else {
				filter.append(" AND a.mac_address LIKE '%").append(form.getMacAddress()).append("%'");
			}
		}

		if (form.getCpumaker() != null && !form.getCpumaker().trim().isEmpty()) {
			filter.append(" AND UPPER(cpu.cpu_manufacturer) = UPPER('")
					.append(form.getCpumaker()).append("')");
		}

		if (form.getCpugeneration() != null && !form.getCpugeneration().trim().isEmpty()) {
			filter.append(" AND UPPER(cpu.cpu_generation) = UPPER('")
					.append(form.getCpugeneration()).append("')");
		}

		if (form.getCpumodel() != null && !form.getCpumodel().trim().isEmpty()) {
			if (Boolean.TRUE.equals(form.getCpumodelExact())) {
				filter.append(" AND UPPER(cpu.cpu_model) = UPPER('")
						.append(form.getCpumodel()).append("')");
			} else {
				filter.append(" AND UPPER(cpu.cpu_model) LIKE UPPER('%")
						.append(form.getCpumodel().toUpperCase()).append("%')");
			}
		}

		if (form.getLessthan2()) ramConditions.add("a.ram_mb < 2048");
		if (form.getBetween2and4()) ramConditions.add("a.ram_mb >= 2048 AND a.ram_mb <= 4096");
		if (form.getBetween4and8()) ramConditions.add("a.ram_mb > 4096 AND a.ram_mb <= 8192");
		if (form.getBetween8and16()) ramConditions.add("a.ram_mb > 8192 AND a.ram_mb <= 16384");
		if (form.getBetween16and32()) ramConditions.add("a.ram_mb > 16384 AND a.ram_mb <= 32768");
		if (form.getMorethan32()) ramConditions.add("a.ram_mb > 32768");

		if (!ramConditions.isEmpty()) {
			filter.append(" AND (");
			for (int i = 0; i < ramConditions.size(); i++) {
				if (i > 0) filter.append(" OR ");
				filter.append(ramConditions.get(i));
			}
			filter.append(")");
		}

		if (form.getWindowssys() != null && !form.getWindowssys().trim().isEmpty()) {
			filter.append(" AND UPPER(os.os_name) = UPPER('")
					.append(form.getWindowssys()).append("')");
		}

		if (form.getOfficesoft() != null && !form.getOfficesoft().trim().isEmpty()) {
			filter.append(" AND UPPER(off.office_software_name) = UPPER('")
					.append(form.getOfficesoft()).append("')");
		}

		if (form.getSecuritysoft() != null && !form.getSecuritysoft().trim().isEmpty()) {
			filter.append(" AND UPPER(sec.security_software_name) = UPPER('")
					.append(form.getSecuritysoft()).append("')");
		}

		if (form.getGreen()) domainConditions.add("グリーン");
		if (form.getInter()) domainConditions.add("インタ");

		if (!domainConditions.isEmpty()) {
			filter.append(" AND (");
			for (int i = 0; i < domainConditions.size(); i++) {
				if (i > 0) filter.append(" OR ");
				filter.append("UPPER(h.holding_name) = UPPER('")
						.append(domainConditions.get(i)).append("')");
			}
			filter.append(")");
		}

		// if (form.getPurdatefrom() != null && !form.getPurdatefrom().isEmpty()) {
		// 	filter.append(" AND a.purchase_date >= DATE '")
		// 			.append(form.getPurdatefrom()).append("'");
		// }

		// if (form.getPurdateto() != null && !form.getPurdateto().isEmpty()) {
		// 	filter.append(" AND a.purchase_date <= DATE '")
		// 			.append(form.getPurdateto()).append("'");
		// }

		String from = form.getPurdatefrom();
		String to = form.getPurdateto();

		if (from != null && !from.isEmpty() && to != null && !to.isEmpty()) {

			LocalDate dateFrom = LocalDate.parse(from);
			LocalDate dateTo = LocalDate.parse(to);

			LocalDate start = dateFrom.isBefore(dateTo) ? dateFrom : dateTo;
			LocalDate end = dateFrom.isBefore(dateTo) ? dateTo : dateFrom;

			filter.append(" AND a.purchase_date >= DATE '").append(start).append("'");
			filter.append(" AND a.purchase_date <= DATE '").append(end).append("'");

		} else {

			if (from != null && !from.isEmpty()) {
				filter.append(" AND a.purchase_date >= DATE '").append(from).append("'");
			}

			if (to != null && !to.isEmpty()) {
				filter.append(" AND a.purchase_date <= DATE '").append(to).append("'");
			}
		}

		if (form.getMonitor() != null && !form.getMonitor().trim().isEmpty()) {
			filter.append(" AND UPPER(mon.monitor_name) = UPPER('")
					.append(form.getMonitor()).append("')");
		}

		if (form.getWithkeyb()) filter.append(" AND a.has_keyboard = 1");
		if (form.getWithmouse()) filter.append(" AND a.has_mouse = 1");
		if (form.getNokeyb()) filter.append(" AND a.has_keyboard = 0");
		if (form.getNomouse()) filter.append(" AND a.has_mouse = 0");

		if (form.getLoc1()) locationConditions.add("名古屋");
		if (form.getLoc2()) locationConditions.add("大阪");
		if (form.getLoc3()) locationConditions.add("東京");
		if (form.getLoc4()) locationConditions.add("浜松");

		if (!locationConditions.isEmpty()) {
			filter.append(" AND (");
			for (int i = 0; i < locationConditions.size(); i++) {
				if (i > 0) filter.append(" OR ");
				filter.append("UPPER(loc.branch) = UPPER('")
						.append(locationConditions.get(i)).append("')");
			}
			filter.append(")");
		}

		locationConditions.clear();

		if (form.getFloor4()) locationConditions.add("4F");
		if (form.getFloor7()) locationConditions.add("7F");

		if (!locationConditions.isEmpty()) {
			filter.append(" AND (");
			for (int i = 0; i < locationConditions.size(); i++) {
				if (i > 0) filter.append(" OR ");
				filter.append("UPPER(loc.floor_no) = UPPER('")
						.append(locationConditions.get(i)).append("')");
			}
			filter.append(")");
		}

		if (form.getRoom() != null && !form.getRoom().trim().isEmpty()) {
			filter.append(" AND UPPER(loc.room_name) = UPPER('")
					.append(form.getRoom()).append("')");
		}

		if (form.getUser() != null && !form.getUser().trim().isEmpty()) {
			if (Boolean.TRUE.equals(form.getUserExact())) {
				filter.append(" AND REPLACE(REPLACE(UPPER(emp.employee_name),'(',''),')','') = UPPER('")
						.append(form.getUser()).append("')");
			} else {
				filter.append(" AND REPLACE(REPLACE(UPPER(emp.employee_name),'(',''),')','') LIKE UPPER('%")
						.append(form.getUser().toUpperCase()).append("%')");
			}
		}

		if (form.getProject() != null && !form.getProject().trim().isEmpty()) {
			filter.append(" AND UPPER(pr.project_name) = UPPER('")
					.append(form.getProject()).append("')");
		}

		if (form.getStatus() != null && !form.getStatus().trim().isEmpty()) {
			filter.append(" AND UPPER(df.flag_desc) = UPPER('")
					.append(form.getStatus()).append("')");
		}

		if (form.getStatuschangeFrom() != null && !form.getStatuschangeFrom().isEmpty()) {
			filter.append(" AND a.status_change_date >= DATE '")
					.append(form.getStatuschangeFrom()).append("'");
		}

		if (form.getStatuschangeTo() != null && !form.getStatuschangeTo().isEmpty()) {
			filter.append(" AND a.status_change_date <= DATE '")
					.append(form.getStatuschangeTo()).append("'");
		}

		if (form.getWin1()) winUpConditions.add("1");
		if (form.getWin2()) winUpConditions.add("2");
		if (form.getWin3()) winUpConditions.add("3");
		if (form.getWin4()) winUpConditions.add("4");
		if (Boolean.TRUE.equals(form.getWin5())) winUpConditions.add("5");

		if (!winUpConditions.isEmpty()) {
			filter.append(" AND (");
			for (int i = 0; i < winUpConditions.size(); i++) {
				if (i > 0) filter.append(" OR ");
				filter.append("wr.flag_id = '").append(winUpConditions.get(i)).append("'");
			}
			filter.append(")");
		}

		return filter.toString();
	}
}