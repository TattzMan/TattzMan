package com.example.project.entities;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor



public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    @Column(unique = true)
    private String candidateNumber;
    @Enumerated(EnumType.STRING)
    private Session session; // Enum for JUNE or NOVEMBER

    @Column(unique = true)
    private String email;
    private String password; // Stored as hashed password
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum Session {
        JUNE, NOVEMBER
    }

}
