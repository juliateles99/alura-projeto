package br.com.alura.ProjetoAlura.registration;

import br.com.alura.ProjetoAlura.course.Course;
import br.com.alura.ProjetoAlura.course.CourseRepository;
import br.com.alura.ProjetoAlura.course.CourseStatus;
import br.com.alura.ProjetoAlura.user.Role;
import br.com.alura.ProjetoAlura.user.User;
import br.com.alura.ProjetoAlura.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private RegistrationService registrationService;

    private User student;
    private User instructor;
    private Course activeCourse;
    private Course inactiveCourse;
    private NewRegistrationDTO validRegistrationDTO;
    private Registration registration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        student = new User("Julia Teles", "julia@example.com", Role.STUDENT, "password");
        instructor = new User("Eduardo Teste", "eduardo@example.com", Role.INSTRUCTOR, "password");

        activeCourse = new Course("Java Course", "java-course", "A course about Java", instructor);
        inactiveCourse = new Course("Spring Course", "spring-course", "A course about Spring", instructor);
        inactiveCourse.inactivate();

        validRegistrationDTO = new NewRegistrationDTO();
        validRegistrationDTO.setCourseCode(activeCourse.getCode());
        validRegistrationDTO.setStudentEmail(student.getEmail());

        registration = new Registration(student, activeCourse);
    }

    @Test
    void createRegistration_WithValidData_ShouldCreateRegistration() {
        when(userRepository.findByEmail(validRegistrationDTO.getStudentEmail())).thenReturn(Optional.of(student));
        when(courseRepository.findByCode(validRegistrationDTO.getCourseCode())).thenReturn(Optional.of(activeCourse));
        when(registrationRepository.existsByUserAndCourse(student, activeCourse)).thenReturn(false);
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        Registration createdRegistration = registrationService.createRegistration(validRegistrationDTO);

        assertNotNull(createdRegistration);
        assertEquals(student, createdRegistration.getUser());
        assertEquals(activeCourse, createdRegistration.getCourse());

        verify(registrationRepository).save(any(Registration.class));
    }

    @Test
    void createRegistration_WithNonExistentStudent_ShouldThrowException() {
        when(userRepository.findByEmail(validRegistrationDTO.getStudentEmail())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            registrationService.createRegistration(validRegistrationDTO);
        });

        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    void createRegistration_WithNonExistentCourse_ShouldThrowException() {
        when(userRepository.findByEmail(validRegistrationDTO.getStudentEmail())).thenReturn(Optional.of(student));
        when(courseRepository.findByCode(validRegistrationDTO.getCourseCode())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            registrationService.createRegistration(validRegistrationDTO);
        });

        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    void createRegistration_WithInactiveCourse_ShouldThrowException() {
        validRegistrationDTO.setCourseCode(inactiveCourse.getCode());

        when(userRepository.findByEmail(validRegistrationDTO.getStudentEmail())).thenReturn(Optional.of(student));
        when(courseRepository.findByCode(validRegistrationDTO.getCourseCode())).thenReturn(Optional.of(inactiveCourse));

        assertThrows(IllegalArgumentException.class, () -> {
            registrationService.createRegistration(validRegistrationDTO);
        });

        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    void createRegistration_WithExistingRegistration_ShouldThrowException() {
        when(userRepository.findByEmail(validRegistrationDTO.getStudentEmail())).thenReturn(Optional.of(student));
        when(courseRepository.findByCode(validRegistrationDTO.getCourseCode())).thenReturn(Optional.of(activeCourse));
        when(registrationRepository.existsByUserAndCourse(student, activeCourse)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            registrationService.createRegistration(validRegistrationDTO);
        });

        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    void generateReport_ShouldReturnReportItems() {
        Object[] result1 = new Object[]{"Java Course", "java-course", "Eduardo Teste", "eduardo@example.com", 10L};
        Object[] result2 = new Object[]{"Spring Course", "spring-course", "Eduardo Teste", "eduardo@example.com", 5L};

        when(registrationRepository.findCoursesWithMostRegistrations()).thenReturn(Arrays.asList(result1, result2));

        List<RegistrationReportItem> reportItems = registrationService.generateReport();

        assertNotNull(reportItems);
        assertEquals(2, reportItems.size());

        assertEquals("Java Course", reportItems.get(0).getCourseName());
        assertEquals("java-course", reportItems.get(0).getCourseCode());
        assertEquals("Eduardo Teste", reportItems.get(0).getInstructorName());
        assertEquals("eduardo@example.com", reportItems.get(0).getInstructorEmail());
        assertEquals(10L, reportItems.get(0).getTotalRegistrations());

        assertEquals("Spring Course", reportItems.get(1).getCourseName());
        assertEquals("spring-course", reportItems.get(1).getCourseCode());
        assertEquals("Eduardo Teste", reportItems.get(1).getInstructorName());
        assertEquals("eduardo@example.com", reportItems.get(1).getInstructorEmail());
        assertEquals(5L, reportItems.get(1).getTotalRegistrations());
    }
}