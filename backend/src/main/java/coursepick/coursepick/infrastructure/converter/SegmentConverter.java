package coursepick.coursepick.infrastructure.converter;

import com.mongodb.client.model.geojson.MultiLineString;
import com.mongodb.client.model.geojson.Position;
import coursepick.coursepick.domain.Segment;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@WritingConverter
@Component
public class SegmentConverter implements Converter<Segment, Document> {

    @Override
    public Document convert(Segment source) {
        if (source == null || source.lines() == null || source.lines().isEmpty()) {
            return new Document("locations", List.of());
        }

        List<MultiLineString> lineStrings = source.lines().stream()
                .map(line -> new MultiLineString(List.of(List.of(
                        new Position(line.start().longitude(), line.start().latitude()),
                        new Position(line.end().longitude(), line.end().latitude())
                ))))
                .collect(Collectors.toList());

        return new Document("locations", lineStrings);
    }
}
