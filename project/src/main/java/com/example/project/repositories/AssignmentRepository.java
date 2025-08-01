package com.example.project.repositories;

import com.example.project.entities.Assignment;
import com.example.project.entities.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long>{
    List<Assignment> findByCourse(Courses course);

}
