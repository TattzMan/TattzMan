package com.example.project.services;

import com.example.project.entities.Courses;
import com.example.project.entities.Enrollment;
import com.example.project.entities.Student;
import com.example.project.repositories.CoursesRepository;
import com.example.project.repositories.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CoursesRepository coursesRepository;


    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CoursesRepository coursesRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.coursesRepository = coursesRepository;
    }

    public Enrollment enrollStudentInCourse(Student student, Courses course) {
        if (enrollmentRepository.findByStudentAndCourse(student, course).isPresent()) {
            throw new IllegalArgumentException("Student already enrolled in this course.");
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setProgress(0.0); // Initial progress
        return enrollmentRepository.save(enrollment);
    }

    public Optional<Enrollment> getEnrollment(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId);
    }

    public List<Enrollment> getEnrollmentsByStudent(Student student) {
        return enrollmentRepository.findByStudent(student);
    }

    public Enrollment updateProgress(Long enrollmentId, Double progress) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        enrollment.setProgress(progress);
        return enrollmentRepository.save(enrollment);
    }

    public Optional<Enrollment> getEnrollment(Student student, Courses course) {
        return null;
    }
}
