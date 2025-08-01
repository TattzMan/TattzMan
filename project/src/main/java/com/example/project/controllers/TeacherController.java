package com.example.project.controllers;
import com.example.project.entities.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import com.example.project.services.*;
import org.springframework.stereotype.Controller;

@Controller
public class TeacherController {

    private final CourseService courseService;
    private MaterialsService materialsService;
    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;
    private final UserService userService;

    public TeacherController(CourseService courseService, MaterialsService materialService,
                             AssignmentService assignmentService, SubmissionService submissionService, UserService userService) {
        this.courseService = courseService;
        this.materialsService = materialsService;
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
        this.userService = userService;
    }

    @GetMapping("teacher/dashboard")
    public String teacherDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        Teacher teacher = userService.findTeacherByEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher not found for email: " + email));
        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", courseService.getAllCourses()); // Teachers can see all courses
        return "teacher/dashboard";
    }

    @GetMapping("teacher/courses/create")
    public String createCourseForm(Model model) {
        model.addAttribute("course", new Courses());
        return "teacher/create-course";
    }

    @PostMapping("teacher/courses")
    public String createCourse(@ModelAttribute Courses course) {
        courseService.createCourse(course);
        return "redirect:/teacher/dashboard?courseCreated=true";
    }

    @GetMapping("teacher/course/{courseId}")
    public String viewCourseDetails(@PathVariable Long courseId, Model model) {
        Courses course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        List<Materials> materials =MaterialsService.getMaterialsByCourse(courseId);
        List<Assignment> assignments = assignmentService.getAssignmentsByCourse(courseId);
        model.addAttribute("course", course);
        model.addAttribute("materials", materials);
        model.addAttribute("assignments", assignments);
        return "teacher/course-details";
    }

    @GetMapping("teacher/materials/upload/{courseId}")
    public String uploadMaterialForm(@PathVariable Long courseId, Model model) {
        model.addAttribute("courseId", courseId);
        return "teacher/upload-material";
    }

    @PostMapping("teacher/materials/upload/{courseId}")
    public String uploadMaterial(@PathVariable Long courseId,
                                 @RequestParam("title") String title,
                                 @RequestParam("file") MultipartFile file,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        String email = userDetails.getUsername();
        Teacher teacher = userService.findTeacherByEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher not found for email: " + email));
        MaterialsService.uploadMaterials(courseId, title, file, teacher);
        model.addAttribute("uploadSuccess", "Material uploaded successfully!");
        return "redirect:/teacher/course/" + courseId;
    }

    @GetMapping("teacher/assignments/create/{courseId}")
    public String createAssignmentForm(@PathVariable Long courseId, Model model) {
        model.addAttribute("courseId", courseId);
        model.addAttribute("assignment", new Assignment());
        return "teacher/create-assignment";
    }

    @PostMapping("/assignments/{courseId}")
    public String createAssignment(@PathVariable Long courseId,
                                   @ModelAttribute Assignment assignment,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Teacher teacher = userService.findTeacherByEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher not found for email: " + email));
        assignmentService.createAssignment(courseId, assignment.getTitle(), assignment.getDescription(), assignment.getDueDate(), teacher);
        return "redirect:/teacher/course/" + courseId;
    }

    @GetMapping("teacher/submissions/{assignmentId}")
    public String viewSubmissions(@PathVariable Long assignmentId, Model model) {
        Assignment assignment = assignmentService.getAssignmentById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        List<Submission> submissions = submissionService.getSubmissionsByAssignment(assignmentId);
        model.addAttribute("assignment", assignment);
        model.addAttribute("submissions", submissions);
        return "teacher/view-submissions";
    }

    @GetMapping("teacher/submissions/mark/{submissionId}")
    public String markSubmissionForm(@PathVariable Long submissionId, Model model) {
        Submission submission = submissionService.getSubmissionById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
        model.addAttribute("submission", submission);
        return "teacher/mark-submission";
    }

    @PostMapping("teacher/submissions/mark/{submissionId}")
    public String markSubmission(@PathVariable Long submissionId,
                                 @RequestParam Double grade,
                                 @RequestParam String feedback,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Teacher teacher = userService.findTeacherByEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher not found for email: " + email));
        submissionService.markSubmission(submissionId, grade, feedback, teacher);
        Long assignmentId = submissionService.getSubmissionById(submissionId).get().getAssignment().getId();
        return "redirect:/teacher/submissions/" + assignmentId;
    }

    @GetMapping("teacher/submissions/download/{submissionId}")
    public ResponseEntity<Resource> downloadSubmission(@PathVariable Long submissionId) throws IOException {
        Submission submission = submissionService.getSubmissionById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        Path filePath = Paths.get(submission.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("File not found or not readable");
        }

        // Determine content type from file name (simple inference)
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream"; // Default if not found
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
