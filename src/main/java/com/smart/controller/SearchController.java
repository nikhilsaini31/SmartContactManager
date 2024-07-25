package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@RestController
public class SearchController {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ContactRepository contactRepo;
	
	// search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal pri){
		
		System.out.println(query);
		System.out.println(pri.getName());
		
		User user = this.userRepo.getUserByUserName(pri.getName());
	//	System.out.println(user);
		
		List<Contact> contacts = this.contactRepo.findByNameContainingAndUser(query, user);
		System.out.println("contacts-"+contacts);
		
		return ResponseEntity.ok(contacts);
		
	}
}
