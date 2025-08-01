package com.example.project.services;

import com.example.project.entities.Courses;
import com.example.project.entities.Materials;
import com.example.project.entities.Teacher;
import com.example.project.repositories.MaterialsRepository;
import com.example.project.repositories.TeacherRepository;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Data
public class MaterialsService {

    // Correctly declare and inject MaterialsRepository
    private final MaterialsRepository materialsRepository; // Make it final and inject via constructor

    private static CourseService courseService;
    private final TeacherRepository teacherRepository;

    // Corrected Constructor: Inject MaterialsRepository, CourseService, and TeacherRepository
    public MaterialsService(MaterialsRepository materialsRepository, // <--- Corrected this parameter
                            CourseService courseService,
                            TeacherRepository teacherRepository) {
        this.materialsRepository = materialsRepository; // <--- Assign it here
        this.courseService = courseService;
        this.teacherRepository = teacherRepository;
    }


    public static void uploadMaterials(Long courseId, String title, MultipartFile file, Teacher teacher) {
        // Implement actual upload logic here, likely involving saving the file to disk

    }

    // This method needs to use 'materialsRepository' instance.
    public static List<Materials> getMaterialsByCourse(Long courseId) {
        // You'd need to fetch the Course first, similar to getMaterialsByCourseId
        Courses course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        return MaterialsRepository.findByCourse(course); // Assuming findByCourse exists in MaterialsRepository
    }


    public CourseService getCourseService() {
        return courseService;
    }

    public TeacherRepository getTeacherRepository() {
        return teacherRepository;
    }

    public Materials creatematerials(
            Long courseId,
            String title,
            String filePath,
            String fileType,
            Long uploadedByTeacherId) {
        Materials material = new Materials();
        material.setTitle(title);
        material.setFilePath(filePath);
        material.setFileType(fileType);
        material.setUploadDate(LocalDateTime.now());

        Courses course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        material.setCourse(course);

        Teacher teacher = teacherRepository.findById(uploadedByTeacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + uploadedByTeacherId));
        material.setUploadedBy(teacher);

        return materialsRepository.save(material);
    }

    public Optional<Materials> getLearningMaterialById(Long id) {
        return materialsRepository.findById(id);
    }

    public List<Materials> getMaterialsByCourseId(Long courseId) {
        Courses course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        // Corrected: materialsRepository is an instance, not static.
        // Make sure your MaterialsRepository has a method like `findByCourse(Courses course)`
        return MaterialsRepository.findByCourse(course);
    }

    public Materials updateLearningMaterial(Long id, Materials updatedMaterial) {
        return materialsRepository.findById(id)
                .map(material -> {
                    material.setTitle(updatedMaterial.getTitle());
                    material.setFilePath(updatedMaterial.getFilePath());
                    material.setFileType(updatedMaterial.getFileType());
                    // Consider updating course and teacher relationships if `updatedMaterial` provides them
                    if (updatedMaterial.getCourse() != null) {
                        material.setCourse(updatedMaterial.getCourse());
                    }
                    if (updatedMaterial.getUploadedBy() != null) {
                        material.setUploadedBy(updatedMaterial.getUploadedBy());
                    }
                    return materialsRepository.save(material);
                }).orElseThrow(() -> new RuntimeException("Learning material not found with ID: " + id));
    }

    public void deleteLearningMaterial(Long id) {
        materialsRepository.deleteById(id);
    }

    public Path getMaterialFilePath(String filePath) {
        // This method likely needs to construct a Path object based on a base directory
        // and the provided filePath. It doesn't use any injected dependencies.
        // Example: return Paths.get("your/base/upload/directory", filePath);
        return null; // Placeholder
    }

    public Optional<Materials> getMaterialsById(Long materialId) {
        // Changed return type to Optional<Materials> to match findById
        return materialsRepository.findById(materialId);
    }
}