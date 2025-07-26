package coursepick.coursepick.presentation.v1.dto;

import coursepick.coursepick.application.dto.CourseResponse;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.RoadType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record GeoJson(
        @Schema(example = "Feature")
        String type,
        GeometryResponse geometry,
        PropertiesResponse properties
) {
    record GeometryResponse(
            @Schema(example = "LineString")
            String type,
            @Schema(example = "[[37.509835, 127.102495], [37.510367, 127.101655], [37.509835, 127.102495]]")
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
            @Schema(example = "1")
            long id,
            @Schema(example = "석촌호수")
            String name,
            @Schema(example = "449.305349")
            double distance,
            @Schema(example = "2613.121514")
            double length,
            @Schema(example = "트레일")
            RoadType roadType,
            @Schema(example = "9.8123")
            double difficulty
    ) {
        public static PropertiesResponse from(long id, String name, double distance, double length, RoadType roadType,
                                              double difficulty) {
            return new PropertiesResponse(id, name, distance, length, roadType, difficulty);
        }
    }

    public static GeoJson from(CourseResponse courseResponse) {
        return new GeoJson(
                "Feature",
                GeometryResponse.from(courseResponse.coordinates()),
                PropertiesResponse.from(
                        courseResponse.id(),
                        courseResponse.name(),
                        courseResponse.distance().value(),
                        courseResponse.length().value(),
                        courseResponse.roadType(),
                        courseResponse.difficulty())
        );
    }

    public static List<GeoJson> from(List<CourseResponse> courseResponses) {
        return courseResponses.stream()
                .map(GeoJson::from)
                .toList();
    }
}
