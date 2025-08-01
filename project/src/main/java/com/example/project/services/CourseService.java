package com.example.project.services;


import com.example.project.entities.Courses;
import org.springframework.stereotype.Service;
import com.example.project.repositories.CoursesRepository;
import java.util.List;
import java.util.Optional;


@Service
public class CourseService {
    private final CoursesRepository courseRepository;

    public CourseService(CoursesRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Courses createCourse(Courses course) {
        return courseRepository.save(course);
    }

    public Optional<Courses> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public List<Courses> getAllCourses() {
        return courseRepository.findAll();
    }

}
