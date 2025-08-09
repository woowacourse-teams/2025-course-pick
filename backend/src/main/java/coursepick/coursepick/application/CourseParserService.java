package coursepick.coursepick.application;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseParser;
import coursepick.coursepick.logging.LogContentCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_FILE_EXTENSION;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseParserService {

    private final List<CourseParser> parsers;

    public List<Course> parse(CourseFile file) {
        CourseParser parser = findParser(file);
        log.debug(LogContentCreator.business("코스 파싱을 시작합니다. 선택된 구현체=" + parser.getClass().getSimpleName()));
        List<Course> result = parser.parse(file);
        log.debug(LogContentCreator.business("%d개의 코스를 파싱했습니다.".formatted(result.size())));
        return result;
    }

    private CourseParser findParser(CourseFile file) {
        return parsers.stream()
                .filter(parser -> parser.canParse(file))
                .findAny()
                .orElseThrow(INVALID_FILE_EXTENSION::create);
    }
}
