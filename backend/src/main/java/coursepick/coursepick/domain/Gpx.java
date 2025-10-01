package coursepick.coursepick.domain;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.logging.LogContent;
import io.jenetics.jpx.*;
import lombok.extern.slf4j.Slf4j;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
@Slf4j
public class Gpx {

    private List<Coordinate> coordinates;
    private final GPX gpx;

    public Gpx(List<Coordinate> coordinates) {
        this.gpx = null;
        this.coordinates = coordinates;
    }

    private Gpx(GPX gpx) {
        this.gpx = gpx;
    }

    public static Gpx from(Course course) {
        Track.Builder trackBuilder = Track.builder();
        TrackSegment.Builder trackSegmentBuilder = TrackSegment.builder();
        course.segments().stream()
                .flatMap(segment -> segment.coordinates().stream())
                .forEach(coordinate -> trackSegmentBuilder.addPoint(builder -> builder
                        .lat(coordinate.latitude())
                        .lon(coordinate.longitude())
                        .ele(coordinate.elevation())
                ));
        trackBuilder.addSegment(trackSegmentBuilder.build());

        GPX gpx = GPX.builder()
                .addTrack(trackBuilder.build())
                .creator("Coursepick - https://github.com/woowacourse-teams/2025-course-pick")
                .metadata(Metadata.builder()
                        .name(course.name().value())
                        .build())
                .build();
        return new Gpx(gpx);
    }

    public static Gpx from(CourseFile file) {
        try {
            GPX gpx = GPX.Reader.of(GPX.Reader.Mode.LENIENT)
                    .read(file.inputStream())
                    .toBuilder()
                    .creator("Coursepick - https://github.com/woowacourse-teams/2025-course-pick")
                    .metadata(Metadata.builder()
                            .name(file.name())
                            .build())
                    .build();
            return new Gpx(gpx);
        } catch (IOException e) {
            throw ErrorType.FILE_PARSING_FAIL.create(e.getMessage());
        }
    }

    public static Gpx from_manual_impl(CourseFile file) {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(file.inputStream());
            Double lat = null, lon = null, ele = null;

            List<Coordinate> coordinates = new ArrayList<>();
            while (reader.hasNext()) {
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    String localName = reader.getLocalName();
                    if ("trkpt".equals(localName)) {
                        lat = Double.parseDouble(reader.getAttributeValue(null, "lat"));
                        lon = Double.parseDouble(reader.getAttributeValue(null, "lon"));
                    } else if ("ele".equals(localName)) {
                        reader.next();
                        if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
                            ele = Double.parseDouble(reader.getText());
                        }
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    if ("trkpt".equals(reader.getLocalName())) {
                        if (lat != null && lon != null) {
                            coordinates.add(new Coordinate(lat, lon, ele));
                        }
                        lat = lon = ele = null;
                    }
                }
            }

            return new Gpx(coordinates);

        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    public String toXmlContent() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            GPX.Writer.DEFAULT.write(gpx, baos);
            return baos.toString();
        } catch (IOException e) {
            log.warn("[EXCEPTION] GPX 파일로 변환에 실패했습니다.", LogContent.exception(e));
            throw new IllegalStateException(e);
        }
    }

    public List<Course> toCourses() {
        boolean isRoutesEmpty = gpx.routes().findAny().isEmpty();
        boolean isTracksEmpty = gpx.tracks().findAny().isEmpty();
        if (isRoutesEmpty && isTracksEmpty) throw new IllegalStateException("gpx 파일의 정보가 비어있습니다. gpx=" + gpx);
        else if (isTracksEmpty) {
            return gpx.routes()
                    .map(route -> new Course(gpx.getMetadata().orElseThrow().getName().orElseThrow(), getCoordinates(route)))
                    .toList();
        } else {
            return gpx.tracks()
                    .map(track -> new Course(gpx.getMetadata().orElseThrow().getName().orElseThrow(), getCoordinates(track)))
                    .toList();
        }
    }

    public List<Course> toCourses_manual() {
        return List.of(new Course("코스이름", coordinates));
    }

    private static List<Coordinate> getCoordinates(Route route) {
        return route.getPoints().stream()
                .map(point -> new Coordinate(
                        point.getLatitude().doubleValue(),
                        point.getLongitude().doubleValue(),
                        point.getElevation().orElse(Length.of(0, Length.Unit.METER)).doubleValue())
                ).toList();
    }

    private static List<Coordinate> getCoordinates(Track track) {
        return track.getSegments().stream()
                .flatMap(segment -> segment.getPoints().stream()
                        .map(point -> new Coordinate(
                                point.getLatitude().doubleValue(),
                                point.getLongitude().doubleValue(),
                                point.getElevation().orElse(Length.of(0, Length.Unit.METER)).doubleValue())
                        )
                ).toList();
    }
}
