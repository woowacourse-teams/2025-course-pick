package coursepick.coursepick.application;

import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseRepository;
import coursepick.coursepick.domain.RoadType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCourseApplicationService {

    private final CourseRepository courseRepository;

    @Transactional
    public void replaceCourse(String courseId, List<List<Double>> rawCoordinates, String name, RoadType roadType) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(ErrorType.NOT_EXIST_COURSE::create);

        List<Coordinate> coordinates = rawCoordinates.stream()
                .map(rawCoordinate -> new Coordinate(rawCoordinate.get(0), rawCoordinate.get(1), rawCoordinate.get(2)))
                .toList();
        course.changeCoordinates(coordinates);
        course.changeName(name);
        course.changeRoadType(roadType);

        courseRepository.save(course);
    }
}
