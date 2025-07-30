package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_FILE_EXTENSION;
import static coursepick.coursepick.application.exception.ErrorType.NOT_EXIST_COURSE;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseApplicationService {

    private final CourseRepository courseRepository;
    private final List<CourseParser> courseParsers;

    @Transactional
    public void parseInputStreamAndSave(InputStream fileStream, String fileExtension) {
        log.info("코스 데이터 가져오기 시작");

        CourseParser courseParser = courseParsers.stream()
                .filter(parser -> parser.canParse(fileExtension))
                .findAny()
                .orElseThrow(INVALID_FILE_EXTENSION::create);

        List<Course> courses = parseCoursesFromFile(fileStream, courseParser);
        saveCourses(courses);
    }

    private void saveCourses(List<Course> courses) {
        List<Course> savedCourses = courseRepository.saveAll(courses);

        log.info("DB에 {} 개의 코스를 저장했습니다", savedCourses.size());
    }

    private static List<Course> parseCoursesFromFile(InputStream fileStream, CourseParser courseParser) {
        List<Course> courses = courseParser.parse(fileStream);

        log.info("{} 개의 코스를 파싱했습니다", courses.size());
        return courses;
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> findNearbyCourses(double mapLatitude, double mapLongitude) {
        final Coordinate mapPosition = new Coordinate(mapLatitude, mapLongitude);
        return courseRepository.findAllHasDistanceWithin(mapPosition, new Meter(1000)).stream()
                .map(CourseResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> findNearbyCourses(double mapLatitude, double mapLongitude, double userLatitude, double userLongitude) {
        final Coordinate mapPosition = new Coordinate(mapLatitude, mapLongitude);
        final Coordinate userPosition = new Coordinate(userLatitude, userLongitude);

        return courseRepository.findAllHasDistanceWithin(mapPosition, new Meter(1000)).stream()
                .map(course -> CourseResponse.from(course, userPosition))
                .toList();
    }

    @Transactional(readOnly = true)
    public Coordinate findClosestCoordinate(long id, double latitude, double longitude) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> NOT_EXIST_COURSE.create(id));

        return course.closestCoordinateFrom(new Coordinate(latitude, longitude));
    }
}
