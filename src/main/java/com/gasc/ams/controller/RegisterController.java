package com.gasc.ams.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gasc.ams.dto.RegistrationForm;
import com.gasc.ams.dto.SearchForm;
import com.gasc.ams.service.RegisterOptionService;
import com.gasc.ams.service.RegisterService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RegisterController {

    private final RegisterService registerser = new RegisterService();
    private final RegisterOptionService registerOptionService = new RegisterOptionService();

    private void loadRegisterOptions(Model model) {
        model.addAttribute("manufacturers", registerOptionService.getOptions("manufacturer"));
        model.addAttribute("memorys", registerOptionService.getOptions("memoryram"));
        model.addAttribute("oss", registerOptionService.getOptions("operating_system"));
        model.addAttribute("models", registerOptionService.getOptionsCascade("pc_model", 3, ""));
        model.addAttribute("pcTypess", registerOptionService.getOptions("device_type"));
        model.addAttribute("offices", registerOptionService.getOptions("office_software"));
        model.addAttribute("domains", registerOptionService.getOptions("company"));
        model.addAttribute("owneds", registerOptionService.getOptions("holding"));
        model.addAttribute("secSofts", registerOptionService.getOptions("security_software"));
        model.addAttribute("monitors", registerOptionService.getOptions("monitor"));
        model.addAttribute("locations", registerOptionService.getOptionsCascade("location", 2, ""));
        model.addAttribute("floors", registerOptionService.getOptionsCascade("location", 3, ""));
        model.addAttribute("rooms", registerOptionService.getOptionsCascade("location", 4, ""));
        model.addAttribute("assigneduser", registerOptionService.getOptions("employee_user"));
        model.addAttribute("projects", registerOptionService.getOptions("project"));
        model.addAttribute("win10PriorityOptions", registerOptionService.getOptions("win10update_flag"));
        model.addAttribute("cpu1s", registerOptionService.getOptionsCascade("cpu", 2, ""));
        model.addAttribute("cpu2s", registerOptionService.getOptionsCascade("cpu", 3, ""));
        model.addAttribute("cpu3s", registerOptionService.getOptionsCascade("cpu", 4, ""));
    }
    
    @GetMapping("/registration")
    public String register(HttpServletRequest request,
                        Model model) {

        model.addAttribute("action","registration");
        model.addAttribute("registrationForm", new RegistrationForm());

       loadRegisterOptions(model);

        String fullUrl =(request.getQueryString() != null? "?" + request.getQueryString(): "");
        int index = fullUrl.lastIndexOf("returnUrl=") + 10; 
        
        if (index > 9){
            model.addAttribute("returnUrl", fullUrl.substring(index, fullUrl.length()));
        } else {
            model.addAttribute("returnUrl", "/search");
        }

        return "register";
    }

    @PostMapping("/update")
    public String updateForm(
            @RequestParam("asset_id") Integer assetId,
            @RequestParam(value = "returnUrl", required = false, defaultValue = "/search") String returnUrl,
            @RequestParam(value = "page", required = false, defaultValue = "1") String page,
            @RequestParam(value = "max", required = false, defaultValue = "1") String max,
            @RequestParam(value = "sort", required = false, defaultValue = "newest") String sort,
            @ModelAttribute("searchForm") SearchForm searchForm,
            Model model) {

        if (assetId == null) {
            model.addAttribute("message", "Asset ID is required");
            model.addAttribute("registrationForm", new RegistrationForm());
            return "register";
        }

        RegistrationForm registrationForm = registerser.getAssetById(assetId);
        if (registrationForm == null) {
            model.addAttribute("message", "No record found for Asset ID " + assetId);
            registrationForm = new RegistrationForm();
        }

        if (returnUrl == null || returnUrl.isEmpty()) {
            returnUrl = "/search";
        } else {
            returnUrl = returnUrl.replaceAll("returnUrl=", "");
            String extend = "page=" + page + "&max=" + max + "&sort=" + sort;
            if(returnUrl.contains("page=") || returnUrl.contains("max=") || returnUrl.contains("sort=")){
                returnUrl = returnUrl.replaceAll("&?page=\\d+", "")
                                     .replaceAll("&?max=\\d+", "")
                                     .replaceAll("&?sort=[^&]+", "");
            }
            if(returnUrl.equals("http://localhost:8081/search") ){
                returnUrl += "?";
                returnUrl += extend;
            } else {
                returnUrl += "&";
                returnUrl += extend;
            }
        }

        model.addAttribute("registrationForm", registrationForm);
        model.addAttribute("action", "update");
        model.addAttribute("returnUrl", returnUrl);

        loadRegisterOptions(model);

        return "register";
    }

    @PostMapping("/registration/submit")
    public String saveRegistration(
            @ModelAttribute RegistrationForm registrationForm,
            @RequestParam(value = "returnUrl", required = false) String returnUrl,
            Model model) {

        model.addAttribute("registrationForm", registrationForm);
        model.addAttribute("action", "registration");

        loadRegisterOptions(model);

        if(registrationForm.getComputerName() == null || registrationForm.getComputerName().trim().isEmpty()){
            model.addAttribute("message", "コンピュータ名 は必須です");
            model.addAttribute("toastMessage", "コンピュータ名 は必須です");
            model.addAttribute("returnUrl", returnUrl);
            model.addAttribute("toastType", "danger");
            model.addAttribute("redirectUrl", null);
            return "register";
        }

        String result = registerser.saveRegistration(registrationForm);

        if ("データが正常に登録されました".equals(result)) {
            model.addAttribute("registrationForm", new RegistrationForm());
            model.addAttribute("toastMessage", result);
            model.addAttribute("toastType", "success");
            model.addAttribute("returnUrl", "/registration");
            model.addAttribute("redirectUrl", returnUrl != null ? returnUrl : "/registration");
        } else {
            model.addAttribute("toastMessage", result);
            model.addAttribute("toastType", "danger");
            model.addAttribute("redirectUrl", null);
        }

        return "register";
    }

    @PostMapping("/update/submit")
    public String handleUpdate(
            @ModelAttribute RegistrationForm registrationForm,
            @ModelAttribute("searchForm") SearchForm searchForm,
            @RequestParam(value = "returnUrl", required = false) String returnUrl,
            Model model,
            RedirectAttributes redirectAttributes) {

        model.addAttribute("registrationForm", registrationForm);
        model.addAttribute("action", "update");

        loadRegisterOptions(model);

        if(registrationForm.getComputerName() == null || registrationForm.getComputerName().trim().isEmpty()){
            model.addAttribute("message", "コンピュータ名 は必須です");
            model.addAttribute("toastMessage", "コンピュータ名 は必須です");
            model.addAttribute("toastType", "danger");
            model.addAttribute("returnUrl", returnUrl);
            model.addAttribute("redirectUrl", "/search");
            return "register";
        }

        String result = registerser.updateRegister(registrationForm);

        if ("データが正常に更新されました".equals(result)) {
            model.addAttribute("toastMessage", result);
            model.addAttribute("toastType", "success");
            model.addAttribute("returnUrl", returnUrl);
            model.addAttribute("redirectUrl", returnUrl != null ? returnUrl : "/search");
        } else {
            model.addAttribute("toastMessage", result);
            model.addAttribute("toastType", "danger");
            model.addAttribute("redirectUrl", null);
        }

        redirectAttributes.addFlashAttribute("searchForm", searchForm);

        return "register";
    }
}