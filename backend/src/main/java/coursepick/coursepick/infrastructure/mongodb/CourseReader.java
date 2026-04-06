package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.CourseName;
import coursepick.coursepick.domain.course.Meter;
import coursepick.coursepick.infrastructure.compressor.DataCompressor;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.Binary;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CourseReader implements Converter<Document, Course> {

    private final DataCompressor dataCompressor;

    @Override
    public Course convert(Document source) {
        List<Coordinate> coordinates = parseCoordinatesFromSource(source);
        List<Coordinate> simplifiedCoordinates = parseSimplifiedCoordinates(source, coordinates);

        return new Course(
                source.getObjectId("_id").toHexString(),
                new CourseName(source.getString("name")),
                coordinates,
                simplifiedCoordinates,
                new Meter(source.getDouble("length"))
        );
    }

    private List<Coordinate> parseCoordinatesFromSource(Document source) {
        Object raw = source.get("coordinates");

        if (raw instanceof Document doc) {
            // 1. 새로운 중첩 압축 구조인 경우 { zip_coordinates: BinData, zip_size: int }
            if (doc.containsKey("zip_coordinates") && doc.containsKey("zip_size")) {
                Binary binary = doc.get("zip_coordinates", Binary.class);
                Integer originalSize = doc.getInteger("zip_size");
                if (binary != null && originalSize != null) {
                    String json = dataCompressor.decompress(binary.getData(), originalSize);
                    return parseCoordinatesFromJson(json);
                }
            }

            // 2. 기존 GeoJSON(LineString) 형식인 경우
            if (doc.containsKey("type") && doc.containsKey("coordinates")) {
                return parseCoordinatesFromGeoJson(doc);
            }
        }

        return List.of();
    }

    private List<Coordinate> parseCoordinatesFromJson(String json) {
        if (json == null || json.length() < 4) return List.of();
        String content = json.substring(2, json.length() - 2);
        String[] pairs = content.split("\\\\],\\\\["); // 이스케이프 주의
        if (pairs.length == 1 && pairs[0].isEmpty()) return List.of();

        List<Coordinate> result = new ArrayList<>();
        try {
            for (String pair : pairs) {
                String[] coords = pair.split(",");
                result.add(new Coordinate(Double.parseDouble(coords[1]), Double.parseDouble(coords[0])));
            }
        } catch (Exception e) {
            // 파싱 실패 시 빈 리스트 (로그 출력 권장)
        }
        return result;
    }

    private List<Coordinate> parseSimplifiedCoordinates(Document source, List<Coordinate> original) {
        Object simplified = source.get("simplifiedCoordinates");
        if (simplified instanceof Document doc) {
            return parseCoordinatesFromGeoJson(doc);
        }
        return original; // 필드가 없으면 원본 반환 (마이그레이션 중 필수)
    }

    private List<Coordinate> parseCoordinatesFromGeoJson(Document source) {
        if (source == null) return List.of();
        List<List<Object>> coordinatesData = (List<List<Object>>) source.get("coordinates");
        if (coordinatesData == null) return List.of();

        return coordinatesData.stream()
                .map(coordinateData -> new Coordinate(
                        ((Number) coordinateData.get(1)).doubleValue(),
                        ((Number) coordinateData.get(0)).doubleValue()
                ))
                .toList();
    }
}
