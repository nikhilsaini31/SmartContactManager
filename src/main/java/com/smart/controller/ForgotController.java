package com.smart.controller;

import java.security.Principal;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	Random random = new Random(1000);// minimum value
	
	// email id from open handler
	
	@RequestMapping("/forgot")
	public String openEmailForm() {
		
		return "forgot_email_form";
	}

	
	// send otp
	@PostMapping("/send-otp")
	public String sentOtp(@RequestParam("email") String email,HttpSession session) {
		
		System.out.println("email-"+email);
		
		// generating otp of 4 digit
		
		int otp = random.nextInt(9999); // max-value
		
		System.out.println("otp-"+otp);
		
		// send otp to email
		
		String subject="OTP SCM";
		String message="Your SmartContactManager OTP is - "+otp+"";
		String to=email;
		
		//--------
		
		// check if user exist or not
		User user = this.userRepository.getUserByUserName(email);
		
		if(user==null) {
			
			session.setAttribute("message", new Message("User does not exist with this email id", "danger"));
			return "redirect:/forgot";
		}
		else {
			
			boolean result = this.emailService.sendEmail(subject, message, to);
			
			if(result) {
				
				session.setAttribute("myotp", otp);
				session.setAttribute("email",email);
				return "verify_otp";
			
			}else {
				
				session.setAttribute("message", new Message("check your email", "danger"));
				return "redirect:/forgot";
			}
			
		}
		
		
		
		
	}
	
	// varify otp
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp")int otp,HttpSession session) {
		
		int myOtp=(int) session.getAttribute("myotp");
		String  email = (String) session.getAttribute("email");
		
		if (myOtp==otp) {
			
			
			return "password_change_form";
			
		}else {
			session.setAttribute("message", new Message("wrong ! OTP", "danger"));
			return "verify_otp";
		}
		
	}
	
	// change password
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword,HttpSession session) {
		
		String  email = (String) session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		
		user.setPassword(passwordEncoder.encode(newpassword));
		this.userRepository.save(user);
		
		session.setAttribute("message", new Message("new password changed successfully", "success"));
		
		return "redirect:/signin"; // login page
	}
}
