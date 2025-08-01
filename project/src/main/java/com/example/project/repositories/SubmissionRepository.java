package com.example.project.repositories;

import com.example.project.entities.Assignment;
import com.example.project.entities.Student;
import com.example.project.entities.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudent(Student student);
    List<Submission> findByAssignment(Assignment assignment);
    Optional<Submission> findByStudentAndAssignment(Student student, Assignment assignment);
}
