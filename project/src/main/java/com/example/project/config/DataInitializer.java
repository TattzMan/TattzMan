package com.example.project.config;

import com.example.project.entities.Admin;
import com.example.project.entities.Student;
import com.example.project.entities.Teacher;
import com.example.project.entities.Courses;
import com.example.project.repositories.AdminRepository;
import com.example.project.repositories.StudentRepository;
import com.example.project.repositories.TeacherRepository;
import com.example.project.repositories.CoursesRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(AdminRepository adminRepository, 
                                 StudentRepository studentRepository,
                                 TeacherRepository teacherRepository,
                                 CoursesRepository coursesRepository,
                                 PasswordEncoder passwordEncoder) {
        return args -> {
            // Create default admin if not exists
            if (adminRepository.findByEmail("admin@system.com").isEmpty()) {
                Admin admin = new Admin();
                admin.setFirstName("System");
                admin.setLastName("Administrator");
                admin.setEmail("admin@system.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("SYSTEM_ADMIN");
                adminRepository.save(admin);
                System.out.println("✅ Default admin created: admin@system.com / admin123");
            }

            // Create sample teacher if not exists
            if (teacherRepository.findByEmail("teacher@demo.com").isEmpty()) {
                Teacher teacher = new Teacher();
                teacher.setFirstName("John");
                teacher.setLastName("Smith");
                teacher.setEmail("teacher@demo.com");
                teacher.setPassword(passwordEncoder.encode("teacher123"));
                teacherRepository.save(teacher);
                System.out.println("✅ Demo teacher created: teacher@demo.com / teacher123");
            }

            // Create sample student if not exists
            if (studentRepository.findByEmail("student@demo.com").isEmpty()) {
                Student student = new Student();
                student.setFirstName("Jane");
                student.setLastName("Doe");
                student.setEmail("student@demo.com");
                student.setPassword(passwordEncoder.encode("student123"));
                student.setCandidateNumber("STU001");
                student.setDateOfBirth(LocalDate.of(2000, 5, 15));
                student.setSession(Student.Session.JUNE);
                studentRepository.save(student);
                System.out.println("✅ Demo student created: student@demo.com / student123");
            }

            // Create sample course if not exists
            if (coursesRepository.count() == 0) {
                Courses course = new Courses();
                course.setCourseName("Introduction to Computer Science");
                course.setDescription("A comprehensive introduction to computer science fundamentals including programming, algorithms, and data structures.");
                coursesRepository.save(course);
                System.out.println("✅ Demo course created: Introduction to Computer Science");
            }

            System.out.println("🚀 Database initialization completed!");
            System.out.println("📋 Login Credentials:");
            System.out.println("   Admin: admin@system.com / admin123");
            System.out.println("   Teacher: teacher@demo.com / teacher123");
            System.out.println("   Student: student@demo.com / student123");
        };
    }
}