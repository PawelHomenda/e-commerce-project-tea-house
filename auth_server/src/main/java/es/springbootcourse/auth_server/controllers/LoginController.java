package es.springbootcourse.auth_server.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class LoginController {

	@GetMapping("/")
	public RedirectView redirectRoot() {
		return new RedirectView("/login");
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/logout")
	public String logout() {
		return "logout";
	}
}