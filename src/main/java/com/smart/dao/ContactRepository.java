package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer>{

	// pageable has 1. current page = page
	//              2. contact per page= 5 
	
	@Query("from Contact as c where c.user.id =:UserId ")
	public Page<Contact> findContactsByUserId(@Param("UserId") int userId,Pageable pePageable );
	
	// seach box result with type-name in login user
	public List<Contact> findByNameContainingAndUser(String name,User user);
	
}
