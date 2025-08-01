package com.example.project.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommonController {
    
    @GetMapping("/")
    public String home() {
        return "welcome";
    }
    
    @GetMapping("/dashboard-redirect")
    public String redirectDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            return "redirect:/student/dashboard";
        } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            return "redirect:/teacher/dashboard";
        } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }
        // Fallback for other roles or unhandled cases
        return "redirect:/login?error=no_dashboard_found";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";
    }
}