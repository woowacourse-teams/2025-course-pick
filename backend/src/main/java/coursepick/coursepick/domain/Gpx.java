package coursepick.coursepick.domain;

import coursepick.coursepick.logging.LogContent;
import io.jenetics.jpx.*;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

    public String toXml() {
        String result;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            GPX.Writer.DEFAULT.write(gpx, baos);
            result = baos.toString();
        } catch (IOException e) {
            log.warn("[EXCEPTION] GPX 파일로 변환에 실패했습니다.", LogContent.exception(e));
            throw new IllegalStateException(e);
        }
        return result;
    }
}
