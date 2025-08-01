package com.example.project.services;


import com.example.project.entities.Assignment;
import com.example.project.entities.Courses;
import com.example.project.entities.Teacher;
import com.example.project.repositories.AssignmentRepository;
import lombok.Data;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
@Data
public class AssignmentService {

    public AssignmentRepository assignmentRepository;
    public CourseService courseService;

    public AssignmentService(AssignmentRepository assignmentRepository, CourseService courseService) {
        this.assignmentRepository = assignmentRepository;
        this.courseService = courseService;
    }

    public Assignment createAssignment(Long courseId, String title, String description, LocalDateTime dueDate, Teacher uploader) {
        Courses course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setDueDate(dueDate);
        assignment.setUploadedBy(uploader);
        return assignmentRepository.save(assignment);
    }

    public List<Assignment> getAssignmentsByCourse(Long courseId) {
        Courses course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
        return assignmentRepository.findByCourse(course);
    }

    public Optional<Assignment> getAssignmentById(Long id) {
        return assignmentRepository.findById(id);
    }
}
