package coursepick.coursepick.infrastructure.converter;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.GeoLine;
import coursepick.coursepick.domain.GeoLineBuilder;
import coursepick.coursepick.domain.Segment;
import coursepick.coursepick.logging.LogContent;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public abstract class SegmentListConverter {

    @WritingConverter
    public static class Writer implements Converter<List<Segment>, Document> {
        @Override
        public Document convert(List<Segment> source) {
            if (source == null) return null;
            List<List<List<Double>>> segmentsData = source.stream()
                    .map(Writer::parseSegment)
                    .toList();

            Document document = new Document();
            document.put("type", "MultiLineString");
            document.put("coordinates", segmentsData);

            return document;
        }

        private static List<List<Double>> parseSegment(Segment segment) {
            List<GeoLine> lines = segment.lines();
            List<List<Double>> lineStringCoordinates = new ArrayList<>();
            lines.forEach(line ->
                    lineStringCoordinates.add(List.of(line.start().longitude(), line.start().latitude(), line.start().elevation()))
            );
            GeoLine lastLine = lines.getLast();
            lineStringCoordinates.add(List.of(lastLine.end().longitude(), lastLine.end().latitude(), lastLine.end().elevation()));

            return lineStringCoordinates;
        }
    }

    @ReadingConverter
    public static class Reader implements Converter<Document, List<Segment>> {
        @Override
        public List<Segment> convert(Document source) {
            try {
                if (source == null) return null;
                List<List<List<Double>>> segmentsData = (List<List<List<Double>>>) source.get("coordinates");

                return segmentsData.stream()
                        .map(Reader::parseSegment)
                        .toList();
            } catch (Exception e) {
                log.warn("[EXCEPTION] 세그먼트 파싱 중 예외 발생", LogContent.exception(source, e));
                return Collections.emptyList();
            }
        }

        private static Segment parseSegment(List<List<Double>> lineStringCoordinates) {
            List<Coordinate> coordinates = lineStringCoordinates.stream()
                    .map(coordinate -> new Coordinate(coordinate.get(1), coordinate.get(0), coordinate.get(2))) // lat, lng, ele
                    .toList();

            return new Segment(GeoLineBuilder.fromCoordinates(coordinates).build());
        }
    }
}
