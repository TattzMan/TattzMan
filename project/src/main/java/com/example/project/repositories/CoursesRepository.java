package com.example.project.repositories;

import com.example.project.entities.Courses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursesRepository  extends JpaRepository<Courses, Long> {
}
