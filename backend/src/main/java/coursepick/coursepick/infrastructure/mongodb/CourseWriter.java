package coursepick.coursepick.infrastructure.mongodb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.Review;
import coursepick.coursepick.infrastructure.compressor.DataCompressor;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public class CourseWriter implements Converter<Course, Document> {

    private final DataCompressor dataCompressor;
    private final ObjectMapper objectMapper;

    @Override
    public Document convert(Course source) {
        Document document = new Document();
        if (source.id() != null && !source.id().isBlank()) {
            document.put("_id", new ObjectId(source.id()));
        }
        document.put("name", source.name().value());

        Document rawCoordinatesDoc = convertCoordinatesToCompressedData(source.coordinates());
        document.put("coordinates", rawCoordinatesDoc);

        document.put("simplifiedCoordinates", convertCoordinatesToGeoJson(source.simplifiedCoordinates()));

        document.put("length", source.length().value());
        document.put("reviews", convertReviewsToDocuments(source.reviews()));
        document.put("creatorId", source.creatorId());
        document.put("reportUserIds", source.reportUserIds());
        if (source.createdAt() != null) {
            document.put("createdAt", Date.from(source.createdAt().atZone(ZoneId.systemDefault()).toInstant()));
        }
        document.put("schemaVersion", 1);
        return document;
    }

    private List<Document> convertReviewsToDocuments(List<Review> reviews) {
        return reviews.stream()
                .map(review -> {
                    Document reviewDoc = new Document();
                    reviewDoc.put("id", review.id());
                    reviewDoc.put("userId", review.userId());
                    reviewDoc.put("authorNickname", review.authorNickname());
                    reviewDoc.put("content", review.content());
                    reviewDoc.put("createdAt", review.createdAt());
                    reviewDoc.put("reportUserIds", review.reportUserIds());
                    return reviewDoc;
                })
                .toList();
    }

    private String convertCoordinatesToJson(List<Coordinate> coordinates) {
        try {
            return objectMapper.writeValueAsString(coordinates);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("좌표 데이터를 JSON으로 변환하는 중 오류 발생", e);
        }
    }

    private Document convertCoordinatesToCompressedData(List<Coordinate> coordinates) {
        String jsonCoordinates = convertCoordinatesToJson(coordinates);

        Document rawCoordinatesDoc = new Document();
        rawCoordinatesDoc.put("zip_coordinates", dataCompressor.compress(jsonCoordinates));
        rawCoordinatesDoc.put("zip_size", jsonCoordinates.getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
        return rawCoordinatesDoc;
    }

    private Document convertCoordinatesToGeoJson(List<Coordinate> coordinates) {
        List<List<Double>> coordinatesData = coordinates.stream()
                .map(coordinate -> List.of(coordinate.longitude(), coordinate.latitude()))
                .toList();

        Document document = new Document();
        document.put("type", "LineString");
        document.put("coordinates", coordinatesData);

        return document;
    }
}
