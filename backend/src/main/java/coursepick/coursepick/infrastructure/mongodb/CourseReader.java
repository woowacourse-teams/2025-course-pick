package coursepick.coursepick.infrastructure.mongodb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import coursepick.coursepick.domain.course.*;
import coursepick.coursepick.infrastructure.compressor.DataCompressor;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.Binary;
import org.springframework.core.convert.converter.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RequiredArgsConstructor
public class CourseReader implements Converter<Document, Course> {

    private final DataCompressor dataCompressor;
    private final ObjectMapper objectMapper;

    @Override
    public Course convert(Document source) {
        List<Coordinate> coordinates = parseCoordinatesFromSource(source);
        List<Coordinate> simplifiedCoordinates = parseSimplifiedCoordinates(source);
        List<Review> reviews = parseReviews(source);

        return new Course(
                source.getObjectId("_id").toHexString(),
                new CourseName(source.getString("name")),
                coordinates,
                simplifiedCoordinates,
                new Meter(source.getDouble("length")),
                reviews,
                source.getString("creatorId"),
                parseReportUserIds(source),
                parseCreatedAt(source)
        );
    }

    private Set<String> parseReportUserIds(Document source) {
        List<String> reportUserIds = source.getList("reportUserIds", String.class);
        if (reportUserIds == null) return new HashSet<>();
        return new HashSet<>(reportUserIds);
    }

    private LocalDateTime parseCreatedAt(Document source) {
        Date date = Optional.ofNullable(source.getDate("createdAt"))
                .orElseGet(() -> source.getObjectId("_id").getDate());

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private List<Coordinate> parseCoordinatesFromSource(Document source) {
        Document rawCoordinates = (Document) source.get("coordinates");

        Binary binary = rawCoordinates.get("zip_coordinates", Binary.class);
        int originalSize = rawCoordinates.getInteger("zip_size");

        String json = dataCompressor.decompress(binary.getData(), originalSize);
        return parseCoordinatesFromJson(json);
    }

    private List<Coordinate> parseCoordinatesFromJson(String json) {
        if (json == null || json.isBlank()) return List.of();

        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON을 좌표 데이터로 변환하는 중 오류 발생", e);
        }
    }

    private List<Coordinate> parseSimplifiedCoordinates(Document source) {
        Document simplified = (Document) source.get("simplifiedCoordinates");
        return parseCoordinatesFromGeoJson(simplified);
    }

    private List<Coordinate> parseCoordinatesFromGeoJson(Document source) {
        List<List<Object>> coordinatesData = (List<List<Object>>) source.get("coordinates");

        return coordinatesData.stream()
                .map(coordinateData -> new Coordinate(
                        ((Number) coordinateData.get(1)).doubleValue(),
                        ((Number) coordinateData.get(0)).doubleValue()
                ))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private List<Review> parseReviews(Document source) {
        List<Document> reviewDocs = (List<Document>) source.get("reviews");
        if (reviewDocs == null || reviewDocs.isEmpty()) {
            return new ArrayList<>();
        }
        List<Review> reviews = new ArrayList<>();
        for (Document reviewDoc : reviewDocs) {
            Set<String> reportUserIds = new HashSet<>(reviewDoc.getList("reportUserIds", String.class, List.of()));

            String id = reviewDoc.getString("id");
            String userId = reviewDoc.getString("userId");
            String authorNickname = reviewDoc.getString("authorNickname");
            String content = reviewDoc.getString("content");
            Instant createdAt = toInstant(reviewDoc.get("createdAt"));
            reviews.add(new Review(id, userId, authorNickname, content, reportUserIds, createdAt));
        }
        return reviews;
    }

    private Instant toInstant(Object value) {
        if (value instanceof Date date) {
            return date.toInstant();
        }
        if (value instanceof Instant instant) {
            return instant;
        }
        return Instant.now();
    }
}
