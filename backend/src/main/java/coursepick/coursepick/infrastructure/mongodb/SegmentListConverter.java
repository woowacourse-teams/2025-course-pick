package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.GeoLine;
import coursepick.coursepick.domain.GeoLineBuilder;
import coursepick.coursepick.domain.Segment;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.ArrayList;
import java.util.List;

public class SegmentListConverter {

    @WritingConverter
    public static class Writing implements Converter<List<Segment>, Document> {
        @Override
        public Document convert(List<Segment> source) {
            List<List<List<Double>>> segmentsData = source.stream()
                    .map(Writing::parseSegment)
                    .toList();

            Document multiLineString = new Document();
            multiLineString.put("type", "MultiLineString");
            multiLineString.put("coordinates", segmentsData);

            return multiLineString;
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
    public static class Reading implements Converter<Document, List<Segment>> {

        @Override
        @SuppressWarnings("unchecked")
        public List<Segment> convert(Document source) {
            List<List<List<Double>>> segmentsData = (List<List<List<Double>>>) source.get("coordinates");

            return segmentsData.stream()
                    .map(Reading::parseSegment)
                    .toList();
        }

        private static Segment parseSegment(List<List<Double>> lineStringCoordinates) {
            List<Coordinate> coordinates = lineStringCoordinates.stream()
                    .map(coord -> new Coordinate(coord.get(1), coord.get(0), coord.get(2))) // lat, lng, ele
                    .toList();

            return new Segment(GeoLineBuilder.fromCoordinates(coordinates).build());
        }
    }
}
