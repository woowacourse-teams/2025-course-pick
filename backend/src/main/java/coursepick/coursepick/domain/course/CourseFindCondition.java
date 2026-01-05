package coursepick.coursepick.domain.course;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class CourseFindCondition {

    private final double mapLatitude;
    private final double mapLongitude;
    private final int scope;
    private final @Nullable Integer minLength;
    private final @Nullable Integer maxLength;
    private final @Nullable List<String> difficulties;
    private final @Nullable Integer pageNumber;

    public Coordinate mapPosition() {
        return new Coordinate(mapLatitude, mapLongitude);
    }

    public Meter scope() {
        return new Meter(scope).clamp(1000, 3000);
    }

    public Pageable pageable() {
        if (pageNumber == null || pageNumber < 0) return PageRequest.of(0, 10);
        else return PageRequest.of(pageNumber, 10);
    }
}
