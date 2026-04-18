package coursepick.coursepick.infrastructure.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import coursepick.coursepick.domain.course.*;
import coursepick.coursepick.domain.user.User;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CourseConverterTest {


    private CourseConverter.Writer writer;
    private CourseConverter.Reader reader;
    private Course course;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        writer = new CourseConverter.Writer(objectMapper);
        reader = new CourseConverter.Reader(objectMapper);
        course = new Course(
                "507f1f77bcf86cd799439011",
                new CourseName("테스트 코스"),
                List.of(new Coordinate(37.5, 127.0), new Coordinate(37.51, 127.01), new Coordinate(37.52, 127.02)),
                List.of(new Coordinate(37.5, 127.0), new Coordinate(37.52, 127.02)),
                new Meter(1500.0),
                List.of(new Review(new User(null, "providerId", "reviewer"), "hi")),
                "creatorId123"
        );

    }


    @Test
    void Course를_Document로_변환한다() {
        Document document = writer.convert(course);

        assertThat(document.get("_id")).isEqualTo(new ObjectId(course.id()));
        assertThat(document.getString("name")).isEqualTo(course.name().value());
        assertThat(document.getDouble("length")).isEqualTo(course.length().value());
        assertThat(document.get("coordinates")).isInstanceOf(Document.class);
        assertThat(document.get("simplifiedCoordinates")).isInstanceOf(Document.class);
        assertThat(document.get("reviews")).isInstanceOf(List.class);
        assertThat(document.getString("creatorId")).isEqualTo(course.creatorId());
    }

    @Test
    void Document를_Course로_변환한다() {
        Document document = writer.convert(course);

        Document coordinatesDoc = (Document) document.get("coordinates");
        coordinatesDoc.put("zip_coordinates", new Binary((byte[]) coordinatesDoc.get("zip_coordinates")));

        Course result = reader.convert(document);

        assertThat(result.id()).isEqualTo(course.id());
        assertThat(result.name()).isEqualTo(course.name());
        assertThat(result.coordinates()).isEqualTo(course.coordinates());
        assertThat(result.simplifiedCoordinates()).isEqualTo(course.simplifiedCoordinates());
        assertThat(result.length()).isEqualTo(course.length());
        assertThat(result.reviews()).hasSameSizeAs(course.reviews());
        assertThat(result.creatorId()).isEqualTo(course.creatorId());
    }
}
