package coursepick.coursepick.infrastructure.compressor;

import coursepick.coursepick.domain.course.Coordinate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.bson.Document;

public class GzipCompressor {

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
