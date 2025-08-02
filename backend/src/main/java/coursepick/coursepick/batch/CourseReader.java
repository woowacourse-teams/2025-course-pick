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

import java.util.Iterator;
import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
public class CourseReader implements ItemReader<Course> {

    private final CourseFileFetcher courseFileFetcher;
    private final CourseParserService courseParserService;

    private Iterator<CourseFile> fileIterator;
    private Iterator<Course> courseIterator;

    @Override
    public Course read() throws UnexpectedInputException, ParseException, NonTransientResourceException {
        if (fileIterator == null) {
            fileIterator = courseFileFetcher.fetchAll().iterator();
        }

        // 현재 파일의 Course 목록을 다 읽었으면 다음 파일 처리
        if (courseIterator == null || !courseIterator.hasNext()) {
            if (fileIterator.hasNext()) {
                CourseFile nextFile = fileIterator.next();
                List<Course> coursesFromFile = courseParserService.parse(nextFile);
                this.courseIterator = coursesFromFile.iterator();
            } else {
                return null; // 모든 파일 처리 완료
            }
        }

        return courseIterator.hasNext() ? courseIterator.next() : read();
    }
}
