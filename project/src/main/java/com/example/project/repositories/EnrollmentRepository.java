package com.example.project.repositories;


import com.example.project.entities.Courses;
import com.example.project.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import com.example.project.entities.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>{
    List<Enrollment> findByStudent(Student student);
    Optional<Enrollment> findByStudentAndCourse(Student student, Courses course);
}
