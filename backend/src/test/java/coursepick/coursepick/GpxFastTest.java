package coursepick.coursepick;

import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.Gpx;
import coursepick.coursepick.test_util.CoordinateTestUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class GpxFastTest {

    private static final Course COURSE = new Course("테스트코스", CoordinateTestUtil.square(0, 0, 0.0001, 0.0001));

    /*
    기존                  : 172, 177, 170
    XmlProvider 구현 후    : 174, 173, 175
     */
    @Test
    void GPX파싱_성능테스트() {
        final int testCount = 10;
        final int testUnitCount = 100000;

        // warm up
        testIt(testRunnable(), testUnitCount);

        // speed test
        long avg = 0;
        for (int i = 0; i < testCount; i++) {
            avg += testIt(testRunnable(), testUnitCount);
        }
        System.out.println("avg : " + (avg / testCount) + "ms");
    }

    long testIt(Runnable target, int time) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < time; i++) {
            target.run();
        }
        long end = System.currentTimeMillis();
        return (end - start);
    }

    @NotNull
    private static Runnable testRunnable() {
        return () -> {
            var sut = Gpx.from(COURSE);
            sut.toCourses();
        };
    }
}
