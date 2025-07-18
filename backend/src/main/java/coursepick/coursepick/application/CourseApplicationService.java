package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.CourseRepository;
import coursepick.coursepick.domain.Meter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseApplicationService {

    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<CourseResponse> findNearbyCourses(double latitude, double longitude) {
        final Coordinate target = new Coordinate(latitude, longitude);
        return courseRepository.findAllHasDistanceWithin(target, new Meter(1000)).stream()
                .map(course -> CourseResponse.from(course, target))
                .toList();
    }
}
