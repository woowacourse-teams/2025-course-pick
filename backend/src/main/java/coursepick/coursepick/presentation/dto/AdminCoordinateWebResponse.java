package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.Segment;

import java.util.ArrayList;
import java.util.List;

public record AdminCoordinateWebResponse(
        double latitude,
        double longitude
) {
    public static List<AdminCoordinateWebResponse> from(List<Coordinate> coordinates) {
        return coordinates.stream()
                .map(AdminCoordinateWebResponse::from)
                .toList();
    }

    public static AdminCoordinateWebResponse from(Coordinate coordinate) {
        return new AdminCoordinateWebResponse(coordinate.latitude(), coordinate.longitude());
    }

    public static List<AdminCoordinateWebResponse> from(Course course) {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        for (Segment segment : course.segments()) {
            if (!coordinates.isEmpty()) {
                coordinates.removeLast();
            }
            coordinates.addAll(segment.coordinates());
        }
        return from(coordinates);
    }
}
