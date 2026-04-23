package com.gasc.ams.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.gasc.ams.dto.Asset;
import com.gasc.ams.dto.SearchForm;
import com.gasc.ams.service.RegisterOptionService;
import com.gasc.ams.service.SearchService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchSer;

    @Autowired
    private RegisterOptionService registerOptionService;

    @GetMapping("/search")
    public String search(@ModelAttribute SearchForm form,
                        @RequestParam(name = "sort", defaultValue = "newest") String sort,
                        @RequestParam(name = "page", defaultValue = "1") int page,
                        @RequestParam(name = "checker", required = false) String checker,
                        Model model,
                        HttpServletRequest request) {

        List<Asset> results = getSortedAssets(sort, "");

        model.addAttribute("results", results);
        model.addAttribute("totalCount", results.size());
        model.addAttribute("checker", "search");

        loadOptions(model);
        model.addAttribute("fullUrl", buildFullUrl(request));

        return "search";
    }

    @GetMapping("/search/sort")
    public String sortResults(SearchForm form,
                            @RequestParam(name = "sort", defaultValue = "newest") String sort,
                            @RequestParam(name = "page", defaultValue = "1") int page,
                            @RequestParam(name = "checker", required = false) String checker,
                            Model model) {

        String filter = searchSer.getSearchFilter(form);
        List<Asset> results = getSortedAssets(sort, filter);

        model.addAttribute("page", page);
        model.addAttribute("sort", sort);
        model.addAttribute("results", results);

        return "search :: resultsBody";
    }

    @GetMapping("/search/filter")
    public String filterResults(@ModelAttribute SearchForm form,
                                @RequestParam(name = "sort", defaultValue = "newest") String sort,
                                @RequestParam(name = "page", defaultValue = "1") int page,
                                @RequestParam(name = "max", defaultValue = "1") int max,
                                @RequestParam(name = "checker", required = false) String checker,
                                Model model,
                                HttpServletRequest request) {

        if (form == null) {
            return "redirect:/search";
        }

        if (max == 0) {
            page = 1;
        }

        String filter = searchSer.getSearchFilter(form);
        List<Asset> results = getSortedAssets(sort, filter);

        model.addAttribute("searchForm", form);
        model.addAttribute("results", results);
        model.addAttribute("totalCount", results.size());
        model.addAttribute("checker", "filter");
        model.addAttribute("page", page);

        loadOptions(model);

        String fullUrl = buildFullUrl(request);
        int index = fullUrl.indexOf("&tpage=");
        model.addAttribute("fullUrl", index == -1 ? fullUrl : fullUrl.substring(0, index));

        return "search";
    }

    private List<Asset> getSortedAssets(String sort, String filter) {
        return switch (sort) {
            case "oldest" -> searchSer.fetchAssets("O", filter);
            case "recentlyUpdated" -> searchSer.fetchAssets("U", filter);
            default -> searchSer.fetchAssets("N", filter);
        };
    }

    private void loadOptions(Model model) {
        model.addAttribute("manufacturers", registerOptionService.getOptions("manufacturer"));
        model.addAttribute("cpuList", "");
        model.addAttribute("cpu1s", registerOptionService.getOptionsCascade("cpu", 2, ""));
        model.addAttribute("cpu2s", registerOptionService.getOptionsCascade("cpu", 3, ""));
        model.addAttribute("cpu3s", registerOptionService.getOptionsCascade("cpu", 4, ""));
        model.addAttribute("deviceTypes", registerOptionService.getOptions("device_type"));
        model.addAttribute("disposalFlags", registerOptionService.getOptions("disposal_flag"));
        model.addAttribute("employeeUsers", registerOptionService.getOptions("employee_user"));
        model.addAttribute("locations", registerOptionService.getOptions("location"));
        model.addAttribute("officeSoftwares", registerOptionService.getOptions("office_software"));
        model.addAttribute("operatingSystems", registerOptionService.getOptions("operating_system"));
        model.addAttribute("securitySofts", registerOptionService.getOptions("security_software"));
        model.addAttribute("monitors", registerOptionService.getOptions("monitor"));
        model.addAttribute("rooms", registerOptionService.getOptionsCascade("location", 4, ""));
        model.addAttribute("projects", registerOptionService.getOptions("project"));
    }

    private String buildFullUrl(HttpServletRequest request) {
        return request.getRequestURL().toString() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : "");
    }
}