package coursepick.coursepick;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.dto.CourseFileExtension;
import coursepick.coursepick.domain.Gpx;
import org.junit.jupiter.api.Test;

public class GpxFastTest {

    private static CourseFile COURSE_FILE;

    /*
    기존                  : 170, 164, 164
    XmlProvider 구현 후    : 159, 163, 161
     */
    @Test
    void GPX파싱_성능테스트() {
        final int testCount = 10;

        // warm up
        for (int i = 0; i < 3; i++) {
            testIt(testRunnable(), testCount);
        }

        // speed test
        long test = testIt(testRunnable(), testCount);
        System.out.println("result : " + test + "ms");
    }

    long testIt(Runnable target, int time) {
        long sum = 0;
        for (int i = 0; i < time; i++) {
            long start = System.currentTimeMillis();
            target.run();
            long end = System.currentTimeMillis();
            sum += end - start;
        }
        return sum / time;
    }

    Runnable testRunnable() {
        return () -> {
            initCourseFile();
            var sut = Gpx.from(COURSE_FILE);
            sut.toCourses();
        };
    }

    private void initCourseFile() {
        COURSE_FILE = new CourseFile(
                "테스트코스",
                CourseFileExtension.GPX,
                getClass().getClassLoader().getResourceAsStream("test.gpx")
        );
    }
}
