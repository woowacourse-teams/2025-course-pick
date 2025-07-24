package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.application.exception.NotFoundException;
import coursepick.coursepick.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseApplicationService {

    private final CourseRepository courseRepository;
    private final CourseParser courseParser;

    @Transactional
    public void parseAndSaveCourses(String filePath) {
        log.info("코스 데이터 가져오기 시작: {}", filePath);

        List<Course> parsedCourses = courseParser.parse(filePath);
        log.info("{} 개의 코스를 파싱했습니다", parsedCourses.size());

        List<Course> savedCourses = courseRepository.saveAll(parsedCourses);

        log.info("DB에 {} 개의 코스를 저장했습니다", savedCourses.size());
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> findNearbyCourses(double latitude, double longitude) {
        final Coordinate target = new Coordinate(latitude, longitude);
        return courseRepository.findAllHasDistanceWithin(target, new Meter(1000)).stream()
                .map(course -> CourseResponse.from(course, target))
                .toList();
    }

    @Transactional(readOnly = true)
    public Coordinate findClosestCoordinate(long id, double latitude, double longitude) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorType.NOT_EXIST_COURSE.message()));

        return course.minDistanceCoordinate(new Coordinate(latitude, longitude));
    }
}
