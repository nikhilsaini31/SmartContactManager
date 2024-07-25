package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepo;

	@RequestMapping("/")
	public String home(Model m) {

		m.addAttribute("title", "Home-smart contact controller");
		return "home";
	}

	@RequestMapping("/about")
	public String about(Model m) {

		m.addAttribute("title", "About - smart contact controller");
		return "about";
	}

	@RequestMapping("/signup")
	public String signup(Model m) {

		m.addAttribute("title", "signup-smart contact controller");
		m.addAttribute("user", new User());
		return "signup";
	}

	// handler for register user
	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User us,BindingResult bres,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model m,
			HttpSession session) {

		try {

			if (!agreement) {
				throw new Exception("you have not agree the term and condition");

			}

			if(bres.hasErrors()) {
				
				System.out.println("ERROR "+ bres.toString());
				
				m.addAttribute("user", us);
				return "signup";
			}
			
			us.setRole("ROLE_USER");
			us.setEnabled(true);
			us.setPassword(passwordEncoder.encode(us.getPassword()));

			System.out.println("agreement-" + agreement);
			System.out.println("user" + us);

			User result = this.userRepo.save(us);

			m.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registed !!", "success"));
			return "signup";

		} catch (Exception e) {
			e.printStackTrace();
			m.addAttribute("user", us);
			session.setAttribute("message", new Message("Somthing went wrong " + e.getMessage(), "danger"));
			return "signup";
		}

	}

	
	// handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model m) {
		
		m.addAttribute("title", "Login page");
		return "login";
	}
}
