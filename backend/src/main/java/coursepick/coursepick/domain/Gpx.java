package coursepick.coursepick.domain;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.logging.LogContent;
import io.jenetics.jpx.*;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
@Slf4j
public class Gpx {

    private final GPX gpx;

    private Gpx(GPX gpx) {
        this.gpx = gpx;
    }

    public static Gpx from(Course course) {
        Route.Builder routeBuilder = Route.builder();
        for (Segment segment : course.segments()) {
            for (Coordinate coordinate : segment.coordinates()) {
                routeBuilder.addPoint(WayPoint.of(
                        Latitude.ofDegrees(coordinate.latitude()),
                        Longitude.ofDegrees(coordinate.longitude())
                ));
            }
        }
        GPX gpx = GPX.builder()
                .addRoute(routeBuilder.build())
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

    public String toXml() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            GPX.Writer.DEFAULT.write(gpx, baos);
            return baos.toString();
        } catch (IOException e) {
            log.warn("[EXCEPTION] GPX 파일로 변환에 실패했습니다.", LogContent.exception(e));
            throw new IllegalStateException(e);
        }
    }

    public List<Course> toCourses() {
        return gpx.routes()
                .map(route -> new Course(gpx.getMetadata().orElseThrow().getName().orElseThrow(), getCoordinates(route)))
                .toList();
    }

    private static List<Coordinate> getCoordinates(Route route) {
        return route.getPoints().stream()
                .map(point -> new Coordinate(
                        point.getLatitude().doubleValue(),
                        point.getLongitude().doubleValue(),
                        point.getElevation().orElse(Length.of(0, Length.Unit.METER)).doubleValue())
                ).toList();
    }
}
