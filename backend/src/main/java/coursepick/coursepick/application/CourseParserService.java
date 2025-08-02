package coursepick.coursepick.application;

import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.FILE_PARSING_FAIL;
import static coursepick.coursepick.application.exception.ErrorType.INVALID_FILE_EXTENSION;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseParserService {

    private final List<CourseParser> parsers;

    public List<Course> parse(File file) {
        String originalFilename = file.getAbsoluteFile().getName();
        String filename = Normalizer.normalize(originalFilename, Normalizer.Form.NFC);
        String fileExtension = StringUtils.getFilenameExtension(originalFilename);
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw FILE_PARSING_FAIL.create(e.getMessage());
        }
        return parse(inputStream, filename, fileExtension);
    }

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
