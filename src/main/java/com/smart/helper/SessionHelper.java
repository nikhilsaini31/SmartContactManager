package com.smart.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class SessionHelper {
	
	public void removeMessageFromSession() {
		
		try {
			
			HttpSession session = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes() ).getRequest().getSession();
			session.removeAttribute("message"); // this 'message from user controller '
			
			System.out.println("message removed successfully");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
