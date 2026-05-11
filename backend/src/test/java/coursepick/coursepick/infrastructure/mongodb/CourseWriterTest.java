package coursepick.coursepick.infrastructure.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import coursepick.coursepick.domain.course.*;
import coursepick.coursepick.infrastructure.compressor.DataCompressor;
import coursepick.coursepick.infrastructure.compressor.ZstdCompressor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static coursepick.coursepick.test_util.CoordinateFixture.coordinates;
import static org.assertj.core.api.Assertions.assertThat;

class CourseWriterTest {

    private CourseWriter courseWriter;
    private Course course;

    @BeforeEach
    void setUp() {
        DataCompressor dataCompressor = new ZstdCompressor();
        ObjectMapper objectMapper = new ObjectMapper();
        courseWriter = new CourseWriter(dataCompressor, objectMapper);

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        course = Course.testBuilder()
                .id("507f1f77bcf86cd799439011")
                .name(new CourseName("테스트 코스"))
                .coordinates(coordinates())
                .simplifiedCoordinates(coordinates())
                .length(new Meter(1500.0))
                .reviews(List.of(Review.testBuilder()
                        .authorNickname("reviewer")
                        .content("hi")
                        .createdAt(Instant.now())
                        .build()))
                .creatorId("creatorId123")
                .reportUserIds(Set.of("reportMan1"))
                .createdAt(now)
                .build();
    }

    @Test
    void Course를_Document로_변환한다() {
        Document document = courseWriter.convert(course);

        assertThat(document.get("_id")).isEqualTo(new ObjectId(course.id()));
        assertThat(document.getString("name")).isEqualTo(course.name().value());
        assertThat(document.getDouble("length")).isEqualTo(course.length().value());
        assertThat(document.get("coordinates")).isInstanceOf(Document.class);
        assertThat(document.get("simplifiedCoordinates")).isInstanceOf(Document.class);
        assertThat(document.get("reviews")).isInstanceOf(List.class);
        assertThat(document.getString("creatorId")).isEqualTo(course.creatorId());

        // mongodb에서 set 타입을 list 타입으로 변환되는 과정을 거치지 않아서 set 타입으로 검증합니다.
        assertThat(document.get("reportUserIds", Set.class)).isNotEmpty();
        assertThat(document.getDate("createdAt")).isEqualTo(
                Date.from(
                        course.createdAt()
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                )
        );
    }

    @Test
    void Course의_createdAt이_null인_경우_Document에_포함하지_않는다() {
        Course course = Course.testBuilder()
                .id("507f1f77bcf86cd799439011")
                .name(new CourseName("테스트 코스"))
                .coordinates(coordinates())
                .simplifiedCoordinates(coordinates())
                .length(new Meter(1500.0))
                .reviews(new ArrayList<>())
                .creatorId("creatorId123")
                .reportUserIds(Set.of("reportMan1"))
                .createdAt(null)
                .build();

        Document document = courseWriter.convert(course);

        assertThat(document.containsKey("createdAt")).isFalse();
    }
}
