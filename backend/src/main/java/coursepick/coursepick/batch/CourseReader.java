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

    private Iterator<CourseFile> courseFileIterator = Collections.emptyIterator();
    private Iterator<Course> courseIterator = Collections.emptyIterator();

    @Override
    public Course read() throws UnexpectedInputException, ParseException, NonTransientResourceException, IOException {
        if (courseIterator.hasNext()) {
            return courseIterator.next();
        }

        if (readNextCourseFile()) {
            return read();
        }

        return null;
    }

    private boolean readNextCourseFile() throws IOException {
        // CourseFileIterator가 비어있다면, 새로운 파일 목록을 fetch한다.
        if (!courseFileIterator.hasNext()) {
            List<CourseFile> nextCourseFiles = courseFileFetcher.fetchNextPage();

            if (nextCourseFiles.isEmpty()) {
                return false; // 더 이상 가져올 파일이 없음
            }

            this.courseFileIterator = nextCourseFiles.iterator();
        }

        // 다음 CourseFile을 가져와서 Courses로 파싱한다.
        try (CourseFile courseFile = courseFileIterator.next()) {
            List<Course> nextCourses = courseParserService.parse(courseFile);
            this.courseIterator = nextCourses.iterator();
        }

        return true; // 성공적으로 다음 CourseFile을 읽었음
    }
}
