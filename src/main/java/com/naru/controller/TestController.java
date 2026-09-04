package com.naru.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

//    @GetMapping("/hello")
//    public String hello() {
//        return "Hello, secured API!";
//    }
//    
    
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin/dashboard")
	public String adminDashboard() {
	    return "Admin only";
	}
	
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/user/profile")
	public String userProfile() {
	    return "Accessed User profile";
	}

	
}