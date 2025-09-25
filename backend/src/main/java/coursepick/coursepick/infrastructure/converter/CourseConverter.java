package coursepick.coursepick.infrastructure.converter;

import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.Difficulty;
import coursepick.coursepick.domain.InclineSummary;
import coursepick.coursepick.domain.RoadType;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public abstract class CourseConverter {

    private static final SegmentListConverter.Reader SEGMENTS_READER = new SegmentListConverter.Reader();
    private static final SegmentListConverter.Writer SEGMENTS_WRITER = new SegmentListConverter.Writer();
    private static final CourseNameConverter.Reader COURSENAME_READER = new CourseNameConverter.Reader();
    private static final CourseNameConverter.Writer COURSENAME_WRITER = new CourseNameConverter.Writer();
    private static final MeterConverter.Reader METER_READER = new MeterConverter.Reader();
    private static final MeterConverter.Writer METER_WRITER = new MeterConverter.Writer();

    @WritingConverter
    public static class Writer implements Converter<Course, Document> {
        @Override
        public Document convert(Course source) {
            Document document = new Document();
            document.put("name", COURSENAME_WRITER.convert(source.name()));
            document.put("road_type", source.roadType().name());
            document.put("incline_summary", source.inclineSummary().name());
            document.put("segments", SEGMENTS_WRITER.convert(source.segments()));
            document.put("length", METER_WRITER.convert(source.length()));
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
                    COURSENAME_READER.convert(source.getString("name")),
                    RoadType.valueOf(source.getString("road_type")),
                    InclineSummary.valueOf(source.getString("incline_summary")),
                    SEGMENTS_READER.convert(source.get("segments", Document.class)),
                    METER_READER.convert(source.getDouble("length")),
                    Difficulty.valueOf(source.getString("difficulty"))
            );
        }
    }
}
