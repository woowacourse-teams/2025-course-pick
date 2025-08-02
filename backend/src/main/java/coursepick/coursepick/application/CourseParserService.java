package coursepick.coursepick.application;

import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_FILE_EXTENSION;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseParserService {

    private final List<CourseParser> parsers;

    public List<Course> parse(InputStream fileStream, String filename, String fileExtension) {
        log.debug("코스를 파싱을 시작합니다.");
        CourseParser parser = findCourseParser(fileExtension);
        log.debug("선택된 CourseParse 구현체={}", parser.getClass().getSimpleName());
        List<Course> result = parser.parse(filename, fileStream);
        log.info("{}개의 코스를 파싱했습니다", result.size());
        return result;
    }

    private CourseParser findCourseParser(String fileExtension) {
        return parsers.stream()
                .filter(parser -> parser.canParse(fileExtension))
                .findAny()
                .orElseThrow(INVALID_FILE_EXTENSION::create);
    }
}
