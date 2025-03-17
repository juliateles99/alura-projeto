package br.com.alura.ProjetoAlura.registration;

import br.com.alura.ProjetoAlura.course.Course;
import br.com.alura.ProjetoAlura.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByUserAndCourse(User user, Course course);

    @Query(nativeQuery = true, value =
            "SELECT c.name as courseName, c.code as courseCode, " +
                    "u.name as instructorName, u.email as instructorEmail, " +
                    "COUNT(r.id) as totalRegistrations " +
                    "FROM Registration r " +
                    "JOIN Course c ON r.courseId = c.id " +
                    "JOIN User u ON c.instructorId = u.id " +
                    "GROUP BY c.id " +
                    "ORDER BY COUNT(r.id) DESC")
    List<Object[]> findCoursesWithMostRegistrations();
}