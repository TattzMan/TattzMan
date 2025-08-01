package com.example.project.controllers;

import com.example.project.entities.*;
import com.example.project.services.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Controller
public class StudentController {
    private final EnrollmentService enrollmentService;
    private final CourseService courseService;
    private final MaterialsService materialsService;
    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;
    private final UserService userService;

    public StudentController(EnrollmentService enrollmentService, CourseService courseService,
                             MaterialsService learningMaterialService, AssignmentService assignmentService,
                             SubmissionService submissionService, UserService userService) {
        this.enrollmentService = enrollmentService;
        this.courseService = courseService;
        this.materialsService = learningMaterialService;
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
        this.userService = userService;
    }
    @GetMapping("/student/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("student", new Student());
        // The "student" object is needed to prevent Thymeleaf from throwing errors
        // when rendering the form, especially for validation.
        return "signup"; // Assumes your HTML file is named 'signup.html'
    }
    @PostMapping("/signup/student")
    public String registerStudent(@ModelAttribute("student") Student student,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        try {
            // Assume you have a userService with a registerStudent method.
            // This method should handle saving the student and hashing the password.
            userService.registerStudent(student);

            // On success, redirect to the login page with a success message.
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (IllegalStateException e) {
            // On failure (e.g., email already exists), stay on the signup page
            // and add the error message to the model to be displayed.
            model.addAttribute("errorMessage", e.getMessage());

            // This line is important to retain the data the user entered in the form.
            model.addAttribute("student", student);

            return "signup"; // Return to the signup page HTML.
        }
    }

    // New, simplified dashboard method
    @GetMapping("/student-dashboard")
    public String showStudentDashboard() {
        return "studentdashboard"; // This will look for a template named studentdashboard.html
    }

    // Existing dashboard with student data
    @GetMapping({"student/dashboard", "student"})
    public String studentDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        Student student = userService.findStudentByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found for email: " + email));

        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student);
        model.addAttribute("student", student);
        model.addAttribute("enrollments", enrollments);
        return "student/dashboard";
    }

    // ... all your other methods ...
    @GetMapping("student/courses")
    public String viewCourses(Model model) {
        List<Courses> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "student/courses";
    }

    @PostMapping("student/enroll/{courseId}")
    public String enrollInCourse(@PathVariable Long courseId, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        Student student = userService.findStudentByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found for email: " + email));
        Courses course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        try {
            enrollmentService.enrollStudentInCourse(student, course);
            return "redirect:/student/dashboard?enrollSuccess=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/student/courses";
        }
    }

    // ... all other methods from your original controller
}