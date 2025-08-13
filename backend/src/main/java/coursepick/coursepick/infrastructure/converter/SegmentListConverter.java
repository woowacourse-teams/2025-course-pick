package coursepick.coursepick.infrastructure.converter;

import com.mongodb.client.model.geojson.MultiLineString;
import com.mongodb.client.model.geojson.Position;
import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.GeoLine;
import coursepick.coursepick.domain.GeoLineBuilder;
import coursepick.coursepick.domain.Segment;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.List;

public class SegmentListConverter {

    @WritingConverter
    public static class Writer implements Converter<List<Segment>, MultiLineString> {
        @Override
        public MultiLineString convert(List<Segment> source) {
            if (source == null || source.isEmpty()) {
                return new MultiLineString(List.of());
            }

            List<List<Position>> positions = source.stream()
                    .map(segment -> segment.coordinates().stream()
                            .map(coordinate -> new Position(coordinate.longitude(), coordinate.latitude(), coordinate.elevation()))
                            .toList())
                    .toList();

            return new MultiLineString(positions);
        }
    }

    @ReadingConverter
    public static class Reader implements Converter<MultiLineString, List<Segment>> {
        @Override
        public List<Segment> convert(MultiLineString source) {
            if (source == null || source.getCoordinates().isEmpty()) {
                return List.of();
            }

            return source.getCoordinates().stream()
                    .map(positions -> {
                        List<Coordinate> coordinates = positions.stream().map(position -> new Coordinate(position.getValues().get(0), position.getValues().get(1), position.getValues().get(2))).toList();
                        List<GeoLine> geoLines = GeoLineBuilder.fromCoordinates(coordinates).build();
                        return new Segment(geoLines);
                    })
                    .toList();
        }
    }
}
