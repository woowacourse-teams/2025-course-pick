package coursepick.coursepick.infrastructure.converter;

import coursepick.coursepick.domain.*;
import coursepick.coursepick.logging.LogContent;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CourseConverter {

    private static final SegmentListReader SEGMENTS_READER = new SegmentListReader();
    private static final SegmentListWriter SEGMENTS_WRITER = new SegmentListWriter();

    @WritingConverter
    public static class Writer implements Converter<Course, Document> {
        @Override
        public Document convert(Course source) {
            Document document = new Document();
            if (source.id() != null && !source.id().isBlank()) {
                document.put("_id", new ObjectId(source.id()));
            }
            document.put("name", source.name().value());
            document.put("road_type", source.roadType().name());
            document.put("incline_summary", source.inclineSummary().name());
            document.put("segments", SEGMENTS_WRITER.convert(source.segments()));
            document.put("length", source.length().value());
            document.put("difficulty", source.difficulty().name());
            return document;
        }
    }

    @ReadingConverter
    public static class Reader implements Converter<Document, Course> {
        @Override
        public Course convert(Document source) {
            return new Course(
                    source.getObjectId("_id").toHexString(),
                    new CourseName(source.getString("name")),
                    RoadType.valueOf(source.getString("road_type")),
                    InclineSummary.valueOf(source.getString("incline_summary")),
                    SEGMENTS_READER.convert(source.get("segments", Document.class)),
                    new Meter(source.getDouble("length")),
                    Difficulty.valueOf(source.getString("difficulty"))
            );
        }
    }

    @WritingConverter
    private static class SegmentListWriter implements Converter<List<Segment>, Document> {
        @Override
        public Document convert(List<Segment> source) {
            if (source == null) return null;
            List<List<List<Double>>> segmentsData = source.stream()
                    .map(SegmentListWriter::parseSegment)
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

    @Slf4j
    @ReadingConverter
    private static class SegmentListReader implements Converter<Document, List<Segment>> {
        @Override
        public List<Segment> convert(Document source) {
            try {
                if (source == null) return null;
                List<List<List<Double>>> segmentsData = (List<List<List<Double>>>) source.get("coordinates");

                return segmentsData.stream()
                        .map(SegmentListReader::parseSegment)
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
