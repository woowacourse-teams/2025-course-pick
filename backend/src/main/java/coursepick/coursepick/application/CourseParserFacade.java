package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.CourseParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_FILE_EXTENSION;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseParserFacade {

    private final List<CourseParser> parsers;

    public List<Course> parse(CourseFile file) {
        CourseParser parser = findParser(file);
        log.debug("코스 파싱을 시작합니다. 선택된 구현체={}", parser.getClass().getSimpleName());
        List<Course> result = parser.parse(file);
        log.debug("{}개의 코스를 파싱했습니다.", result.size());
        return result;
    }

    private CourseParser findParser(CourseFile file) {
        return parsers.stream()
                .filter(parser -> parser.canParse(file))
                .findAny()
                .orElseThrow(INVALID_FILE_EXTENSION::create);
    }
}
