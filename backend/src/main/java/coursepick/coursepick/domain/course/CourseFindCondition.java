package coursepick.coursepick.domain.course;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class CourseFindCondition {

    private static final int PAGE_SIZE = 10;

    private final double mapLatitude;
    private final double mapLongitude;
    private final int scope;
    private final @Nullable Integer minLength;
    private final @Nullable Integer maxLength;
    private final @Nullable Integer pageNumber;
    private final @Nullable String creatorId;


    public CourseFindCondition(double mapLatitude,
                               double mapLongitude,
                               @Nullable Integer scope,
                               @Nullable Integer minLength,
                               @Nullable Integer maxLength,
                               @Nullable Integer pageNumber
    ) {
        this.mapLatitude = mapLatitude;
        this.mapLongitude = mapLongitude;
        this.scope = scope;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.pageNumber = pageNumber;
        this.creatorId = null;
    }

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

    public Pageable pageable() {
        if (pageNumber == null || pageNumber < 0) return PageRequest.of(0, PAGE_SIZE);
        else return PageRequest.of(pageNumber, PAGE_SIZE);
    }

    public int pageSize() {
        return PAGE_SIZE;
    }

    public String creatorId() {
        return creatorId;
    }
}
