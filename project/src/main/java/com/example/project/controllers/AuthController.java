package com.example.project.controllers;


import com.example.project.entities.Student;
import com.example.project.entities.Teacher;
import com.example.project.services.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(Model model, @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("loginError", "Invalid username or password.");
        }
        return "login";
    }//

    @GetMapping("/student/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup/student")
    public String registerStudent(@RequestParam String firstName,
                                  @RequestParam String lastName,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
                                  @RequestParam String candidateNumber,
                                  @RequestParam Student.Session session,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  Model model) {
        try {
            userService.registerStudent(firstName, lastName, dateOfBirth, candidateNumber, session, email, password);
            model.addAttribute("successMessage", "Student registered successfully! Please login.");
            return "login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }

    @PostMapping("/signup/teacher")
    public String registerTeacher(@RequestParam String firstName,
                                  @RequestParam String lastName,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  Model model) {
        try {
            userService.registerTeacher(firstName, lastName, email, password);
            model.addAttribute("successMessage", "Teacher registered successfully! Please login.");
            return "login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }
}
