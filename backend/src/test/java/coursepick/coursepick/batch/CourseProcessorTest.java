package coursepick.coursepick.batch;

import coursepick.coursepick.domain.*;
import coursepick.coursepick.test_util.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CourseProcessorTest extends AbstractIntegrationTest {

    @Autowired
    CourseProcessor sut;

    @Test
    void 코스의_hash가_다르다면_그대로_반환한다() {
        var course1 = new Course("6105f25199c4ef0380c7f74d", new CourseName("신림천 코스"), RoadType.트레일, InclineSummary.CONTINUOUS_DOWNHILL, List.of(
                new Segment(List.of(new GeoLine(new Coordinate(33.602500, 126.967000), new Coordinate(33.702500, 126.967000)))),
                new Segment(List.of(new GeoLine(new Coordinate(33.602500, 126.967000), new Coordinate(33.702500, 126.967000)))),
                new Segment(List.of(new GeoLine(new Coordinate(33.602500, 126.967000), new Coordinate(33.702500, 126.967000)))),
                new Segment(List.of(new GeoLine(new Coordinate(33.602500, 126.967000), new Coordinate(33.702500, 126.967000))))
        ), new Meter(10.0), Difficulty.보통);
        var course2 = new Course("6105f25199c4ef0380c7f74d", new CourseName("중랑천 코스"), RoadType.트레일, InclineSummary.CONTINUOUS_DOWNHILL, List.of(
                new Segment(List.of(new GeoLine(new Coordinate(33.602500, 126.967000), new Coordinate(33.702500, 126.967000)))),
                new Segment(List.of(new GeoLine(new Coordinate(33.602500, 126.967000), new Coordinate(33.702500, 126.967000)))),
                new Segment(List.of(new GeoLine(new Coordinate(33.602500, 126.967000), new Coordinate(33.702500, 126.967000)))),
                new Segment(List.of(new GeoLine(new Coordinate(33.602500, 126.967000), new Coordinate(33.702500, 126.967000))))
        ), new Meter(10.0), Difficulty.보통);
        dbUtil.saveCourse(course1);

        Course actual = sut.process(course2);

        assertThat(actual).isEqualTo(course2);
    }

    @Test
    void 동일한_hash값을_가졌다면_넘어간다() {
        var course = new Course("6105f25199c4ef0380c7f74d", new CourseName("신림천 코스"), RoadType.트레일, InclineSummary.CONTINUOUS_DOWNHILL, List.of(
                new Segment(List.of(new GeoLine(new Coordinate(33.602500, 126.967000), new Coordinate(33.702500, 126.967000)))),
                new Segment(List.of(new GeoLine(new Coordinate(33.602500, 126.967000), new Coordinate(33.702500, 126.967000)))),
                new Segment(List.of(new GeoLine(new Coordinate(33.602500, 126.967000), new Coordinate(33.702500, 126.967000)))),
                new Segment(List.of(new GeoLine(new Coordinate(33.602500, 126.967000), new Coordinate(33.702500, 126.967000))))
        ), new Meter(10.0), Difficulty.보통);
        dbUtil.saveCourse(course);

        Course actual = sut.process(course);

        assertThat(actual).isEqualTo(null);
    }

    @Test
    void 코스의_id가_null이면_그대로_반환한다() {
        var course = new Course("신림천 코스", RoadType.트레일, List.of(
                new Coordinate(33.602500, 126.967000),
                new Coordinate(33.603000, 126.968000),
                new Coordinate(33.603500, 126.969000),
                new Coordinate(33.602500, 126.967000)
        ));

        Course actual = sut.process(course);

        assertThat(actual).isEqualTo(course);
    }

    @Test
    void 조회_시_없는_코스에_대해서_그대로_반환한다() {
        var course = new Course("test_id", new CourseName("신림천 코스"), RoadType.트레일, InclineSummary.CONTINUOUS_DOWNHILL, List.of(
                new Segment(List.of(new GeoLine(new Coordinate(33.602500, 126.967000), new Coordinate(33.702500, 126.967000)))),
                new Segment(List.of(new GeoLine(new Coordinate(33.602500, 126.967000), new Coordinate(33.702500, 126.967000)))),
                new Segment(List.of(new GeoLine(new Coordinate(33.602500, 126.967000), new Coordinate(33.702500, 126.967000)))),
                new Segment(List.of(new GeoLine(new Coordinate(33.602500, 126.967000), new Coordinate(33.702500, 126.967000))))
        ), new Meter(10.0), Difficulty.보통);

        Course actual = sut.process(course);

        assertThat(actual).isEqualTo(course);
    }
}
