package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.CourseName;
import coursepick.coursepick.domain.course.Meter;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import java.util.ArrayList;

import java.util.zip.GZIPInputStream;

import org.bson.Document;
import org.bson.types.Binary;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

public class CourseReader implements Converter<Document, Course> {

    @Override
    public Course convert(Document source) {
        try {
            return new Course(
                    source.getObjectId("_id").toHexString(),
                    new CourseName(source.getString("name")),
                    parseByteCoordinates(source.get("coordinates", Document.class)),
                    parseCoordinates(source.get("simplifiedCoordinates", Document.class)),
                    new Meter(source.getDouble("length"))
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Coordinate> parseCoordinates(Document source) {
        List<List<Object>> coordinatesData = (List<List<Object>>) source.get("coordinates");

        return coordinatesData.stream()
                .map(coordinateData -> new Coordinate(
                        ((Number) coordinateData.get(1)).doubleValue(),
                        ((Number) coordinateData.get(0)).doubleValue()
                ))
                .toList();
    }

    public List<Coordinate> parseByteCoordinates(Document source) throws IOException {
        Binary byteCoordinates = (Binary) source.get("coordinates");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        GZIPInputStream gzipInStream = new GZIPInputStream(
                new BufferedInputStream(new ByteArrayInputStream(byteCoordinates.getData())));

        int size = 0;
        byte[] buffer = new byte[1024];
        while ( (size = gzipInStream.read(buffer)) > 0 ) {
            outStream.write(buffer, 0, size);
        }
        outStream.flush();
        outStream.close();


        List<List<Double>> coordinatesData = decompress(outStream.toByteArray());

        return coordinatesData.stream()
                .map(coordinateData -> new Coordinate(
                        coordinateData.get(1),
                        coordinateData.get(0)
                ))
                .toList();
    }

    public static List<List<Double>> decompress(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int count = buffer.getInt(); // 첫 4바이트에서 개수 읽기

        List<List<Double>> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(List.of(buffer.getDouble(), buffer.getDouble()));
        }
        return result;
    }
}
