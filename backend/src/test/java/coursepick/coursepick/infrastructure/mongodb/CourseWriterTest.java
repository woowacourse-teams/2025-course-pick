package coursepick.coursepick.infrastructure.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import coursepick.coursepick.domain.course.*;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.infrastructure.compressor.DataCompressor;
import coursepick.coursepick.infrastructure.compressor.ZstdCompressor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

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

        course = new Course(
                "507f1f77bcf86cd799439011",
                new CourseName("테스트 코스"),
                List.of(new Coordinate(37.5, 127.0), new Coordinate(37.51, 127.01), new Coordinate(37.52, 127.02)),
                List.of(new Coordinate(37.5, 127.0), new Coordinate(37.52, 127.02)),
                new Meter(1500.0),
                List.of(new Review(new User(null, "providerId", "reviewer"), "hi")),
                "creatorId123",
                Set.of("reportMan1"),
                now
        );
    }

    @Test
    void Course를_Document로_변환한다() {
        Document document = courseWriter.convert(course);

        assertThat(document.get("_id")).isEqualTo(new ObjectId(course.id()));
        assertThat(document.getString("name")).isEqualTo(course.name().value());
        assertThat(document.getDouble("length")).isEqualTo(course.length().value());
        assertThat(document.get("coordinates")).isInstanceOf(Document.class);
        assertThat(document.get("simplifiedCoordinates")).isInstanceOf(Document.class);

        List<Document> reviews = document.getList("reviews", Document.class);
        assertThat(reviews).hasSize(1);
        Document reviewDoc = reviews.get(0);
        Review originalReview = course.reviews().get(0);
        assertThat(reviewDoc.getString("id")).isEqualTo(originalReview.id());
        assertThat(reviewDoc.getString("userId")).isEqualTo(originalReview.userId());
        assertThat(reviewDoc.getString("authorNickname")).isEqualTo(originalReview.authorNickname());
        assertThat(reviewDoc.getString("content")).isEqualTo(originalReview.content());
        assertThat(reviewDoc.get("createdAt")).isEqualTo(originalReview.createdAt());
        // mongodb에서 set 타입을 list 타입으로 변환되는 과정을 거치지 않아서 set 타입으로 검증합니다.
        assertThat(reviewDoc.get("reportUserIds", Set.class)).isEqualTo(originalReview.reportUserIds());

        assertThat(document.getString("creatorId")).isEqualTo(course.creatorId());

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
        Course course = new Course(
                "507f1f77bcf86cd799439011",
                new CourseName("테스트 코스"),
                List.of(new Coordinate(37.5, 127.0), new Coordinate(37.51, 127.01), new Coordinate(37.52, 127.02)),
                List.of(new Coordinate(37.5, 127.0), new Coordinate(37.52, 127.02)),
                new Meter(1500.0),
                List.of(new Review(new User(null, "providerId", "reviewer"), "hi")),
                "creatorId123",
                Set.of("reportMan1"),
                null
        );

        Document document = courseWriter.convert(course);

        assertThat(document.containsKey("createdAt")).isFalse();
    }
}
