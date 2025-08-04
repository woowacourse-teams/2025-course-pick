package coursepick.coursepick.batch;

import coursepick.coursepick.application.CourseParserService;
import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.domain.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
public class CourseReader implements ItemReader<Course> {

    private final CourseFileFetcher courseFileFetcher;
    private final CourseParserService courseParserService;

    private Iterator<Course> courseIterator = Collections.emptyIterator();

    @Override
    public Course read() throws UnexpectedInputException, ParseException, NonTransientResourceException {
        if (!courseIterator.hasNext()) {
            fetchAndPrepareNextCourses();
        }

        if (courseIterator.hasNext()) {
            return courseIterator.next();
        } else {
            return null;
        }
    }

    private void fetchAndPrepareNextCourses() {
        List<CourseFile> courseFiles = courseFileFetcher.fetchNextPage();

        List<Course> nextCourses = courseFiles.stream()
                .flatMap(file -> courseParserService.parse(file).stream())
                .toList();

        closeInputStreamOf(courseFiles);

        this.courseIterator = nextCourses.iterator();
    }

    private static void closeInputStreamOf(List<CourseFile> courseFiles) {
        courseFiles.forEach(file -> {
            try {
                file.inputStream().close();
            } catch (IOException e) {
                throw new RuntimeException("파일 스트림을 닫는 중 예외가 발생했습니다.", e);
            }
        });
    }
}
