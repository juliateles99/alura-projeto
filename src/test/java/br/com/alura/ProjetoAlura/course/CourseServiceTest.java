package br.com.alura.ProjetoAlura.course;

import br.com.alura.ProjetoAlura.user.Role;
import br.com.alura.ProjetoAlura.user.User;
import br.com.alura.ProjetoAlura.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseService courseService;

    private User instructor;
    private User student;
    private NewCourseDTO validCourseDTO;
    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        instructor = new User("Júlia Teles", "julia@example.com", Role.INSTRUCTOR, "password");
        student = new User("Alex Turner", "alex@example.com", Role.STUDENT, "password");

        validCourseDTO = new NewCourseDTO();
        validCourseDTO.setName("Java Course");
        validCourseDTO.setCode("java-course");
        validCourseDTO.setDescription("A course about Java");
        validCourseDTO.setInstructorEmail("julia@example.com");

        course = new Course(
                validCourseDTO.getName(),
                validCourseDTO.getCode(),
                validCourseDTO.getDescription(),
                instructor
        );
    }

    @Test
    void createCourse_WithValidData_ShouldCreateCourse() {
        when(userRepository.findByEmail(validCourseDTO.getInstructorEmail())).thenReturn(Optional.of(instructor));
        when(courseRepository.existsByCode(validCourseDTO.getCode())).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course createdCourse = courseService.createCourse(validCourseDTO);

        assertNotNull(createdCourse);
        assertEquals(validCourseDTO.getName(), createdCourse.getName());
        assertEquals(validCourseDTO.getCode(), createdCourse.getCode());
        assertEquals(validCourseDTO.getDescription(), createdCourse.getDescription());
        assertEquals(instructor, createdCourse.getInstructor());
        assertEquals(CourseStatus.ACTIVE, createdCourse.getStatus());

        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createCourse_WithNonExistentInstructor_ShouldThrowException() {
        when(userRepository.findByEmail(validCourseDTO.getInstructorEmail())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            courseService.createCourse(validCourseDTO);
        });

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void createCourse_WithNonInstructorUser_ShouldThrowException() {
        when(userRepository.findByEmail(validCourseDTO.getInstructorEmail())).thenReturn(Optional.of(student));

        assertThrows(IllegalArgumentException.class, () -> {
            courseService.createCourse(validCourseDTO);
        });

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void createCourse_WithExistingCode_ShouldThrowException() {
        when(courseRepository.existsByCode(validCourseDTO.getCode())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            courseService.createCourse(validCourseDTO);
        });

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void createCourse_WithInvalidCode_ShouldThrowException() {
        validCourseDTO.setCode("invalid code");

        assertThrows(IllegalArgumentException.class, () -> {
            courseService.createCourse(validCourseDTO);
        });

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void inactivateCourse_WithExistingCourse_ShouldInactivateCourse() {
        when(courseRepository.findByCode(course.getCode())).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course inactivatedCourse = courseService.inactivateCourse(course.getCode());

        assertNotNull(inactivatedCourse);
        assertEquals(CourseStatus.INACTIVE, inactivatedCourse.getStatus());
        assertNotNull(inactivatedCourse.getInactivationDate());

        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void inactivateCourse_WithNonExistentCourse_ShouldThrowException() {
        when(courseRepository.findByCode(course.getCode())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            courseService.inactivateCourse(course.getCode());
        });

        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void inactivateCourse_WithAlreadyInactiveCourse_ShouldThrowException() {
        course.inactivate();
        when(courseRepository.findByCode(course.getCode())).thenReturn(Optional.of(course));

        assertThrows(IllegalStateException.class, () -> {
            courseService.inactivateCourse(course.getCode());
        });

        verify(courseRepository, never()).save(any(Course.class));
    }
}