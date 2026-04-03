package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public abstract class CourseConverter {

    private static final CourseReader COURSE_READER = new CourseReader();

    @WritingConverter
    public static class Writer implements Converter<Course, Document> {
        @Override
        public Document convert(Course source) {
            Document document = new Document();
            if (source.id() != null && !source.id().isBlank()) {
                document.put("_id", new ObjectId(source.id()));
            }
            document.put("name", source.name().value());
            try {
                document.put("coordinates", convertCoordinatesToByteArray(source.coordinates()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            document.put("simplifiedCoordinates", convertCoordinatesToGeoJson(source.simplifiedCoordinates()));
            document.put("length", source.length().value());
            document.put("schemaVersion", 1);
            return document;
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

        private Document convertCoordinatesToByteArray(List<Coordinate> coordinates) throws IOException {
            List<List<Double>> coordinatesData = coordinates.stream()
                    .map(coordinate -> List.of(coordinate.longitude(), coordinate.latitude()))
                    .toList();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(compress(coordinatesData));
            gzipOutputStream.flush();
            gzipOutputStream.close();

            Document document = new Document();
            document.put("coordinates", byteArrayOutputStream.toByteArray());

            return document;
        }

        public static byte[] compress(List<List<Double>> data) {
            int count = data.size();
            // 헤더(개수 정보 4바이트) + (경도 8바이트 + 위도 8바이트) * 개수
            ByteBuffer buffer = ByteBuffer.allocate(4 + (count * 2 * 8));

            buffer.putInt(count); // 좌표가 몇 개인지 먼저 기록 (복원용)
            for (List<Double> coord : data) {
                buffer.putDouble(coord.get(0)); // Longitude
                buffer.putDouble(coord.get(1)); // Latitude
            }
            return buffer.array();
        }
    }

    @ReadingConverter
    public static class Reader implements Converter<Document, Course> {
        @Override
        public Course convert(Document source) {
            return COURSE_READER.convert(source);
        }
    }
}
