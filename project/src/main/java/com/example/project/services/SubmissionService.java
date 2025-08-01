package com.example.project.services;

import com.example.project.entities.Assignment;
import com.example.project.entities.Student;
import com.example.project.entities.Submission;
import com.example.project.entities.Teacher;
import com.example.project.repositories.SubmissionRepository;
import lombok.Data;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Data
public class SubmissionService {
    private String uploadDir;

    public SubmissionRepository submissionRepository;
    public AssignmentService assignmentService;
    public UserService userService; // To get student/teacher details
    private Long submissionId;
    private Double grade;
    private String feedback;
    private Teacher marker;

    public SubmissionService(SubmissionRepository submissionRepository, AssignmentService assignmentService, UserService userService) {
        this.submissionRepository = submissionRepository;
        this.assignmentService = assignmentService;
        this.userService = userService;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public SubmissionRepository getSubmissionRepository() {
        return submissionRepository;
    }

    public void setSubmissionRepository(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    public AssignmentService getAssignmentService() {
        return assignmentService;
    }

    public void setAssignmentService(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void submitAssignment(Long assignmentId, Long studentId, MultipartFile file) throws IOException {
        Assignment assignment = assignmentService.getAssignmentById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found with ID: " + assignmentId));
        Student student = userService.findStudentByEmail(getAuthenticatedUserEmail()) // Replace with actual user ID lookup
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));

        // Check if student already submitted for this assignment
        if (submissionRepository.findByStudentAndAssignment(student, assignment).isPresent()) {
            throw new IllegalStateException("You have already submitted for this assignment.");
        }

        Path uploadPath = Paths.get(uploadDir, "submissions"); // Separate directory for submissions
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setFilePath(filePath.toString());
        submissionRepository.save(submission);
    }

    public Submission markSubmission(Long submissionId, Double grade, String feedback, Teacher marker) {
        this.submissionId = submissionId;
        this.grade = grade;
        this.feedback = feedback;
        this.marker = marker;
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        submission.setGrade(grade);
        submission.setFeedback(feedback);
        submission.setMarkedBy(marker);
        submission.setMarkedDate(LocalDateTime.now());
        return submissionRepository.save(submission);
    }

    public List<Submission> getSubmissionsByAssignment(Long assignmentId) {
        Assignment assignment = assignmentService.getAssignmentById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found with ID: " + assignmentId));
        return submissionRepository.findByAssignment(assignment);
    }

    public List<Submission> getSubmissionsByStudent(Long studentId) {
        Student student = userService.findStudentByEmail(getAuthenticatedUserEmail()) // Replace with actual user ID lookup
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));
        return submissionRepository.findByStudent(student);
    }

    public Optional<Submission> getSubmissionById(Long id) {
        return submissionRepository.findById(id);
    }

    // Helper to get authenticated user email (will be refined with Spring Security)
    private String getAuthenticatedUserEmail() {
        // This is a placeholder. In a real app, you'd get this from Spring Security context.
        // For now, you might hardcode for testing or pass it in.
        // Example: Authentication = SecurityContextHolder.getContext().getAuthentication();
        // return authentication.getName(); // This would be the email if using email as username
        return "student@example.com"; // Placeholder
    }
}
