package coursepick.coursepick.infrastructure;

import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseParser;
import coursepick.coursepick.domain.RoadType;
import coursepick.coursepick.domain.CircleCourse;
import coursepick.coursepick.domain.LineCourse;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Track;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class GpxCourseParser implements CourseParser {

    @Override
    public boolean canParse(String fileExtension) {
        return fileExtension.equals("gpx");
    }

    @Override
    public List<Course> parse(InputStream fileStream) {
        GPX gpx;

        try {
            gpx = GPX.Reader.of(GPX.Reader.Mode.LENIENT).read(fileStream);
        } catch (IOException e) {
            throw ErrorType.FILE_PARSING_FAIL.create(e.getMessage());
        }

        return gpx.tracks()
            .map(GpxCourseParser::createCourseBy)
            .toList();
    }

    private static Course createCourseBy(Track track) {
        String trackName = track.getName().orElse("Default");

        List<Coordinate> coordinates = getCoordinates(track);
        validateCoordinatesIsEmpty(coordinates);
        if (coordinates.getFirst().hasSameLatitudeAndLongitude(coordinates.getLast())) {
            return new CircleCourse(trackName, RoadType.알수없음, coordinates);
        }

        return new LineCourse(trackName, RoadType.알수없음, coordinates);
    }

    private static void validateCoordinatesIsEmpty(List<Coordinate> coordinates) {
        if (coordinates.isEmpty()) {
            throw new IllegalArgumentException(ErrorType.INVALID_FILE_EXTENSION.message());
        }
    }

    private static List<Coordinate> getCoordinates(Track track) {
        return track.getSegments().stream()
                .flatMap(segment -> segment.getPoints().stream()
                        .map(point -> new Coordinate(
                                point.getLatitude().doubleValue(),
                                point.getLongitude().doubleValue(),
                                point.getElevation().orElse(Length.of(0, Length.Unit.METER)).doubleValue())
                        ).distinct()
                ).toList();
    }
}
