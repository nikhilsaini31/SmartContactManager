
package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class userController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;

	// method for adding common data to reponse
	@ModelAttribute
	public void addCommonData(Model model, Principal pri) {

		String userName = pri.getName(); // after filling the login form we'll get username(email)
		System.out.println("USERNAME - " + userName);

		// get the user using username(email)

		User user = userRepository.getUserByUserName(userName);
		System.out.println(user);

		model.addAttribute("user", user);

	}

	@RequestMapping("/index")
	public String dashboard(Model m) {

		m.addAttribute("title", "User Deshboard");
		return "normal/user_deshboard";

	}

	// open add form handler

	@GetMapping("/add-contact")
	public String openContactForm(Model m) {

		m.addAttribute("title", "Add Contact");
		m.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// processing add contact form handler

	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact con,
			                    @RequestParam("profileimage") MultipartFile file,
			                    Principal pr,HttpSession session) {

		try {
			
			String name = pr.getName();
			User user = userRepository.getUserByUserName(name);

			// proccessing and uploading file.
			
			if(file.isEmpty()) {
				
				System.out.println("image is emmpty");
				con.setImage("Default-Profile.png");
				
			}else {
				
				// upload the file to folder and update the name to contact
				
				con.setImage(file.getOriginalFilename());
				
				File saveFile = new ClassPathResource("static/images").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				System.out.println("image is uploded");
			}
			
			
			// save user in contact
			con.setUser(user);
			// and save contact in user (by directionol mapping)
			user.getContacts().add(con);

			userRepository.save(user);

			System.out.println("user added to databases");
			
			// message
			 // Remove the session attribute
	        
			session.setAttribute("message", new Message("contact added successfully ! add more ", "success"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
			session.setAttribute("message", new Message("somthing went wrong ! try again ", "danger"));

		}

		return "normal/add_contact_form";
	}
	
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model m,Principal pri) {
		
		m.addAttribute("title", "Show-contacts");
		
		// for sending list to show
		String userName = pri.getName();
		
		User user = this.userRepository.getUserByUserName(userName);
		
		Pageable pageable = PageRequest.of(page, 5);
		
		// pageable has 1. current page = page
		//              2. contact per page= 5 
		
		Page<Contact> contacts = this.contactRepository.findContactsByUserId(user.getId() , pageable);
		
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentpage", page);
		m.addAttribute("totalpage", contacts.getTotalPages());
		
		return "normal/show-contact";
	}
	
	
	// showing a perticular contact
	
	@GetMapping("/contact/{cId}")
	public String showPerticularContacts(@PathVariable("cId") int cId,Model m ,Principal pri) {
		
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		
		
		// for security
		String userName = pri.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		if(user.getId()==contact.getUser().getId()) {
		
			m.addAttribute("contact", contact);
			m.addAttribute("title", contact.getName());
		}
		return "normal/show_perticular_contact";
	}
	
	
	//  contact delete controller
	
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") int cid,Model m,HttpSession session) {
		
		
		Contact contact = this.contactRepository.findById(cid).get();
		
		contact.setUser(null);
		
		this.contactRepository.delete(contact);
		System.out.println("Deleted");
		
		session.setAttribute("message", new Message("Contact deleted successfully", "success"));
		
		return "redirect:/user/show-contacts/0";
	}
	
	// open  contact update-form controller
	
	@PostMapping("/update/{cid}")
	public String updateContact(@PathVariable("cid") int cid,Model m) {
		
		m.addAttribute("title", "update-form");
		Contact contact = this.contactRepository.findById(cid).get();
		
		m.addAttribute("contact", contact);
		
		return "normal/update_form";
	}
	
	
	// update conatact handler
	
	@PostMapping("/process-update")
	public String  update(@ModelAttribute Contact contact,@RequestParam("profileimage") MultipartFile file,
			Model m,Principal pri,HttpSession session) {
		
		try {
			
			// old contact details
			
			Contact oldContact = this.contactRepository.findById(contact.getCid()).get();
			
			
			//image
			if(!file.isEmpty()) {
				
				// first delete old photo
				File deleteFile = new ClassPathResource("static/images").getFile();
				File oldfile=new File(deleteFile, oldContact.getImage());
				oldfile.delete();
				
				// then update new photo
				  
				File saveFile = new ClassPathResource("static/images").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
				
			}else {
				
				contact.setImage(oldContact.getImage());
				
			}
			
			
			// update
			User user = this.userRepository.getUserByUserName(pri.getName());
			contact.setUser(user);
			
			this.contactRepository.save(contact);
			
			session.setAttribute("message", new Message("Contact updated successfully", "success"));
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		System.out.println(contact.getName());
		return "redirect:/user/contact/"+contact.getCid();
	}
	
	// user profile
	@GetMapping("/profile")
	public String userProfile(Model m ) {
		
		m.addAttribute("title","profile page");
		return "normal/profile";
	}
	
	
	// setting
	@GetMapping("/settings")
	public String openSetting() {
		
		return "normal/setting";
	}

	// change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldpassword") String oldPassword,@RequestParam("newpassword") String newpassword,
								 Principal pri,HttpSession session) {
		
		System.out.println(oldPassword);
		System.out.println(newpassword);
		
		User currentUser = this.userRepository.getUserByUserName(pri.getName());
		
		if(passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
			
			// change password
			
			currentUser.setPassword(passwordEncoder.encode(newpassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("your password is successfully changed", "success"));
			
		}else {
			
			// error..
			session.setAttribute("message", new Message("Wrong ! old password", "danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/index";
	}
}
