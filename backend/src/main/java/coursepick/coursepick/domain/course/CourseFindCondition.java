package coursepick.coursepick.domain.course;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    public @Nullable Meter minLength() {
        if (minLength == null) return null;

        return new Meter(minLength);
    }

    public @Nullable Meter maxLength() {
        if (maxLength == null) return null;

        return new Meter(maxLength);
    }

    public @Nullable List<Difficulty> difficulties() {
        if (difficulties == null) return null;

        return difficulties.stream()
                .map(Difficulty::fromEngName)
                .toList();
    }

    public Pageable pageable() {
        if (pageNumber == null || pageNumber < 0) return PageRequest.of(0, 10);
        else return PageRequest.of(pageNumber, pageSize());
    }

    public int pageSize() {
        return 10;
    }
}
