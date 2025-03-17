package br.com.alura.ProjetoAlura.registration;

import br.com.alura.ProjetoAlura.course.Course;
import br.com.alura.ProjetoAlura.course.CourseRepository;
import br.com.alura.ProjetoAlura.user.User;
import br.com.alura.ProjetoAlura.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public RegistrationService(RegistrationRepository registrationRepository,
                               UserRepository userRepository,
                               CourseRepository courseRepository) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public Registration createRegistration(NewRegistrationDTO newRegistrationDTO) {
        User student = userRepository.findByEmail(newRegistrationDTO.getStudentEmail())
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        Course course = courseRepository.findByCode(newRegistrationDTO.getCourseCode())
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        validateRegistration(student, course);

        Registration registration = new Registration(student, course);
        return registrationRepository.save(registration);
    }

    public List<RegistrationReportItem> generateReport() {
        List<Object[]> results = registrationRepository.findCoursesWithMostRegistrations();
        List<RegistrationReportItem> reportItems = new ArrayList<>();

        for (Object[] result : results) {
            String courseName = (String) result[0];
            String courseCode = (String) result[1];
            String instructorName = (String) result[2];
            String instructorEmail = (String) result[3];
            Long totalRegistrations = ((Number) result[4]).longValue();

            reportItems.add(new RegistrationReportItem(
                    courseName,
                    courseCode,
                    instructorName,
                    instructorEmail,
                    totalRegistrations
            ));
        }

        return reportItems;
    }

    private void validateRegistration(User student, Course course) {
        if (registrationRepository.existsByUserAndCourse(student, course)) {
            throw new IllegalArgumentException("Student is already registered in this course");
        }

        if (!course.isActive()) {
            throw new IllegalArgumentException("Cannot register in an inactive course");
        }
    }
}