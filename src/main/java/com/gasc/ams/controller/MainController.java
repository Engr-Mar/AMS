package com.gasc.ams.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gasc.ams.service.UserRegistration;



@Controller
public class MainController {

	@Autowired
	UserRegistration userReg;

	@GetMapping("/")
	public String home(Model model) {
		return "login";
	}

	@GetMapping("/login")
	public String login(Model model) {
		return "login";
	}

	@GetMapping("/profile")
	public String menu(Model model) {
		return "profile";
	}

	@GetMapping("/error")
	public String error(Model model) {
		return "error";
	}

	@GetMapping("/register")
	public String registerUserPage(Model model) {
		model.addAttribute("activeTab", "register");
		return "userregister";
	}

	@PostMapping("/register/add")
	public String registerUser(
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam("confirmpassword") String confirmpassword,
			Model model) {

		String message = "登録に失敗しました";
		String messageType = "error";

		if (username == null || username.isEmpty()) {
			message = "ユーザー名は空欄にできません";
		} 
		else if (password == null || password.isEmpty()) {
			message = "パスワードは空欄にできません";
		} 
		else if (!password.equals(confirmpassword)) {
			message = "パスワードが一致しません";
		} 
		else if (userReg.checkUserNameExist(username)) {
			message = "ユーザー名は既に存在します";
		} 
		else if (userReg.registerAdmin(username, password)) {
			message = "登録成功！ログインしてください。";
			messageType = "success";
		} 

		model.addAttribute("message", message);
		model.addAttribute("messageType", messageType);

		return "userregister";
	}

}
