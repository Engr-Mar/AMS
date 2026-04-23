package com.gasc.ams.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gasc.ams.service.RegisterOptionService;

@RestController
@RequestMapping("/api/register")
public class RestRegisterController {

    private final RegisterOptionService registeropser = new RegisterOptionService();
    private final String tableName = "cpu";

    @GetMapping("/generation")
    public List<String> generation(
            @RequestParam(name = "manufacturer", required = false) String manufacturer) {

        int columnIndex = 3;
        StringBuilder filter = new StringBuilder();

        if (manufacturer != null && !manufacturer.trim().isEmpty()) {
            filter.append(" WHERE UPPER(cpu_manufacturer) = UPPER('")
                  .append(manufacturer)
                  .append("')");
        }

        return registeropser.getOptionsCascade(tableName, columnIndex, filter.toString());
    }

    @GetMapping("/models")
    public List<String> models(
            @RequestParam(name = "manufacturer", required = false) String manufacturer,
            @RequestParam(name = "generation", required = false) String generation) {

        int columnIndex = 4;
        StringBuilder filter = new StringBuilder();
        boolean hasWhere = false;

        if (manufacturer != null && !manufacturer.trim().isEmpty()) {
            filter.append(" WHERE UPPER(cpu_manufacturer) = UPPER('")
                  .append(manufacturer)
                  .append("')");
            hasWhere = true;
        }

        if (generation != null && !generation.trim().isEmpty()) {
            filter.append(hasWhere ? " AND " : " WHERE ")
                  .append("UPPER(cpu_generation) = UPPER('")
                  .append(generation)
                  .append("')");
        }

        return registeropser.getOptionsCascade(tableName, columnIndex, filter.toString());
    }
}

