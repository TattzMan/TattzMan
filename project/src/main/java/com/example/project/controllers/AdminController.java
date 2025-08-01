package com.example.project.controllers;

import com.example.project.entities.Admin;
import com.example.project.entities.Student;
import com.example.project.entities.Teacher;
import com.example.project.entities.Courses;
import com.example.project.services.UserService;
import com.example.project.services.CourseService;
import com.example.project.repositories.StudentRepository;
import com.example.project.repositories.TeacherRepository;
import com.example.project.repositories.AdminRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AdminRepository adminRepository;

    public AdminController(UserService userService, CourseService courseService, 
                          StudentRepository studentRepository, TeacherRepository teacherRepository,
                          AdminRepository adminRepository) {
        this.userService = userService;
        this.courseService = courseService;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.adminRepository = adminRepository;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found for email: " + email));

        // Get system statistics
        List<Student> students = studentRepository.findAll();
        List<Teacher> teachers = teacherRepository.findAll();
        List<Courses> courses = courseService.getAllCourses();

        model.addAttribute("admin", admin);
        model.addAttribute("totalStudents", students.size());
        model.addAttribute("totalTeachers", teachers.size());
        model.addAttribute("totalCourses", courses.size());
        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);
        model.addAttribute("courses", courses);

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<Student> students = studentRepository.findAll();
        List<Teacher> teachers = teacherRepository.findAll();
        
        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);
        
        return "admin/users";
    }

    @GetMapping("/courses")
    public String manageCourses(Model model) {
        List<Courses> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "admin/courses";
    }

    @GetMapping("/profile")
    public String adminProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found for email: " + email));
        
        model.addAttribute("admin", admin);
        return "admin/profile";
    }

    @PostMapping("/profile/update")
    public String updateAdminProfile(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam String firstName,
                                   @RequestParam String lastName,
                                   Model model) {
        String email = userDetails.getUsername();
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found for email: " + email));
        
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        adminRepository.save(admin);
        
        model.addAttribute("successMessage", "Profile updated successfully!");
        model.addAttribute("admin", admin);
        return "admin/profile";
    }

    @PostMapping("/users/delete/{type}/{id}")
    public String deleteUser(@PathVariable String type, @PathVariable Long id) {
        if ("student".equals(type)) {
            studentRepository.deleteById(id);
        } else if ("teacher".equals(type)) {
            teacherRepository.deleteById(id);
        }
        return "redirect:/admin/users?deleted=true";
    }
}