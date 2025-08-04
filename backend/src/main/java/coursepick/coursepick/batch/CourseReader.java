package coursepick.coursepick.batch;

import coursepick.coursepick.application.CourseParserService;
import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.domain.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.Collections;
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

    @BeforeStep
    public void open() {
        this.fileIterator = courseFileFetcher.fetchAll().iterator();
        this.courseIterator = Collections.emptyIterator();
    }

    @Override
    public Course read() throws UnexpectedInputException, ParseException, NonTransientResourceException {
        while (true) {
            if (courseIterator.hasNext()) {
                return courseIterator.next();
            }

            if (!fileIterator.hasNext()) {
                return null;
            }

            moveToNextFile();
        }
    }

    private void moveToNextFile() {
        CourseFile nextFile = fileIterator.next();
        List<Course> coursesFromFile = courseParserService.parse(nextFile);
        this.courseIterator = coursesFromFile.iterator();
    }
}
