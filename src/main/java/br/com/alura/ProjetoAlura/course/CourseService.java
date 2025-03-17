package br.com.alura.ProjetoAlura.course;

import br.com.alura.ProjetoAlura.user.Role;
import br.com.alura.ProjetoAlura.user.User;
import br.com.alura.ProjetoAlura.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class CourseService {

    private static final Pattern CODE_PATTERN = Pattern.compile("^[a-zA-Z-]+$");
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public Course createCourse(NewCourseDTO newCourseDTO) {
        validateCourseCode(newCourseDTO.getCode());

        User instructor = userRepository.findByEmail(newCourseDTO.getInstructorEmail())
                .orElseThrow(() -> new EntityNotFoundException("Instructor not found"));

        validateInstructor(instructor);

        Course course = new Course(
                newCourseDTO.getName(),
                newCourseDTO.getCode(),
                newCourseDTO.getDescription(),
                instructor
        );

        return courseRepository.save(course);
    }

    public Course inactivateCourse(String courseCode) {
        Course course = courseRepository.findByCode(courseCode)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        if (course.getStatus() == CourseStatus.INACTIVE) {
            throw new IllegalStateException("Course is already inactive");
        }

        course.inactivate();
        return courseRepository.save(course);
    }

    private void validateCourseCode(String code) {
        if (courseRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Course code already exists");
        }

        if (!CODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException("Course code must contain only letters and hyphens");
        }
    }

    private void validateInstructor(User instructor) {
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new IllegalArgumentException("Only instructors can create courses");
        }
    }
}