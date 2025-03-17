package br.com.alura.ProjetoAlura.course;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/course/new")
    public ResponseEntity<Course> createCourse(@Valid @RequestBody NewCourseDTO newCourse) {
        Course course = courseService.createCourse(newCourse);
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    @PostMapping("/course/{code}/inactive")
    public ResponseEntity<Course> inactivateCourse(@PathVariable("code") String courseCode) {
        Course course = courseService.inactivateCourse(courseCode);
        return ResponseEntity.ok(course);
    }
}
