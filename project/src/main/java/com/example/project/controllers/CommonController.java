package com.example.project.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;

public class CommonController {
    @GetMapping("/dashboard-redirect")
    public String redirectDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            return "redirect:/student/dashboard";
        } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            return "redirect:/teacher/dashboard";
        }
        // Fallback for other roles or unhandled cases
        return "redirect:/login?error=no_dashboard_found";
    }

    // Make sure your /login and /signup/** paths are correct too
    @GetMapping("/login")
    public  String showLoginPage() {
        return "login"; // Your login page template
    }

    // Example for signup if you have one
    @GetMapping("/signup")
    public  String showSignupPage() {
        return "signup"; // Your signup page template
    }
}


