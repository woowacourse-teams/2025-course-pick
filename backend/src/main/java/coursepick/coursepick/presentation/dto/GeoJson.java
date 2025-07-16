package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.Coordinate;

import java.util.List;

public record GeoJson(
        String type,
        GeometryResponse geometry,
        PropertiesResponse properties
) {
    record GeometryResponse(
            String type,
            List<List<Double>> coordinates
    ) {
        public static GeometryResponse from(List<Coordinate> coordinates) {
            return new GeometryResponse(
                    "LineString",
                    coordinates.stream()
                            .map(coordinate -> List.of(coordinate.latitude(), coordinate.longitude()))
                            .toList()
            );
        }
    }

    record PropertiesResponse(
            String name,
            double distance,
            double length
    ) {
        public static PropertiesResponse from(String name, double distance, double length) {
            return new PropertiesResponse(name, distance, length);
        }
    }

    public static GeoJson from(CourseResponse courseResponse) {
        return new GeoJson(
                "Feature",
                GeometryResponse.from(courseResponse.coordinates()),
                PropertiesResponse.from(courseResponse.name(), courseResponse.distance(), courseResponse.length())
        );
    }

    public static List<GeoJson> from(List<CourseResponse> courseResponses) {
        return courseResponses.stream()
                .map(GeoJson::from)
                .toList();
    }
}
