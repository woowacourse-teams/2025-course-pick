package coursepick.coursepick;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.Gpx;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GpxFastTest {

    @Test
    void Gpx_깩체를_Course로_변환한다() {
        final int testCount = 100;
        final int testUnitCount = 100000;

        // warm up
        testIt(testRunnable(), testUnitCount);

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
            var coordinates = List.of(
                    new Coordinate(0, 0, 0),
                    new Coordinate(0.0001, 0.0001, 0.0001),
                    new Coordinate(0, 0, 0)
            );
            var course = new Course("테스트코스", coordinates);
            var sut = Gpx.from(course);

            sut.toCourses();
        };
    }
}
