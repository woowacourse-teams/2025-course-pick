package coursepick.coursepick.infrastructure;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseParser;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Track;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class GpxCourseParser implements CourseParser {

    @Override
    public boolean canParse(String fileExtension) {
        return fileExtension.equals("gpx");
    }

    @SneakyThrows
    public List<Course> parse(InputStream fileStream) {
        GPX gpx = GPX.Reader.of(GPX.Reader.Mode.LENIENT).read(fileStream);

        return gpx.tracks()
                .map(track -> new Course(track.getName().orElse("Default"), getCoordinates(track)))
                .toList();
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
