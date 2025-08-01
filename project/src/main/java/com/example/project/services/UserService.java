package com.example.project.services;



import com.example.project.entities.Student;
import com.example.project.entities.Teacher;
import com.example.project.repositories.StudentRepository;
import com.example.project.repositories.TeacherRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;


@Service
public class UserService {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(StudentRepository studentRepository, TeacherRepository teacherRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Student registerStudent(String firstName, String lastName, LocalDate dateOfBirth,
                                   String candidateNumber, Student.Session session, String email, String password) {
        if (studentRepository.findByEmail(email).isPresent() || studentRepository.findByCandidateNumber(candidateNumber).isPresent()) {
            throw new IllegalArgumentException("Student with this email or candidate number already exists.");
        }
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setDateOfBirth(dateOfBirth);
        student.setCandidateNumber(candidateNumber);
        student.setSession(session);
        student.setEmail(email);
        student.setPassword(passwordEncoder.encode(password)); // Hash the password
        return studentRepository.save(student);
    }

    public Teacher registerTeacher(String firstName, String lastName, String email, String password) {
        if (teacherRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Teacher with this email already exists.");
        }
        Teacher teacher = new Teacher();
        teacher.setFirstName(firstName);
        teacher.setLastName(lastName);
        teacher.setEmail(email);
        teacher.setPassword(passwordEncoder.encode(password)); // Hash the password
        return teacherRepository.save(teacher);
    }

    public Optional<Student> findStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    public Optional<Teacher> findTeacherByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }

    public void registerStudent(Student student) {

    }
}


