package br.com.alura.ProjetoAlura.course;

import br.com.alura.ProjetoAlura.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt = LocalDateTime.now();
    private String name;
    private String code;
    private String description;

    @Enumerated(EnumType.STRING)
    private CourseStatus status = CourseStatus.ACTIVE;

    private LocalDateTime inactivationDate;

    @ManyToOne
    @JoinColumn(name = "instructorId")
    private User instructor;

    @Deprecated
    public Course() {}

    public Course(String name, String code, String description, User instructor) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.instructor = instructor;
    }

    public void inactivate() {
        this.status = CourseStatus.INACTIVE;
        this.inactivationDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public LocalDateTime getInactivationDate() {
        return inactivationDate;
    }

    public User getInstructor() {
        return instructor;
    }

    public boolean isActive() {
        return this.status == CourseStatus.ACTIVE;
    }
}