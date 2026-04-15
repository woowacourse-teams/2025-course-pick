package coursepick.coursepick.infrastructure.mongodb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.CourseName;
import coursepick.coursepick.domain.course.Meter;
import coursepick.coursepick.infrastructure.compressor.DataCompressor;
import lombok.RequiredArgsConstructor;
import coursepick.coursepick.domain.course.*;
import org.bson.Document;
import org.bson.types.Binary;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

@RequiredArgsConstructor
public class CourseReader implements Converter<Document, Course> {

    private final DataCompressor dataCompressor;
    private final ObjectMapper objectMapper;

    @Override
    public Course convert(Document source) {
        List<Coordinate> coordinates = parseCoordinatesFromSource(source);
        List<Coordinate> simplifiedCoordinates = parseSimplifiedCoordinates(source);

        return new Course(
                source.getObjectId("_id").toHexString(),
                new CourseName(source.getString("name")),
                coordinates,
                simplifiedCoordinates,
                new Meter(source.getDouble("length")),
                source.getString("creatorId")
        );
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
            return objectMapper.readValue(json, new TypeReference<List<Coordinate>>() {});
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
}
