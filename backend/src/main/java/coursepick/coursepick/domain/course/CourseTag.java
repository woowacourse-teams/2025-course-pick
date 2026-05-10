package coursepick.coursepick.domain.course;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum CourseTag {

    NIGHT_VIEW("야경이 좋은"),
    FLAT("평탄한"),
    HILLY("언덕이 있는"),
    RIVERSIDE("강변"),
    PARK("공원"),
    URBAN("도심"),
    QUIET("한적한"),
    CROWDED("사람이 많은"),
    SCENIC("풍경이 좋은"),
    SHADY("그늘이 많은"),
    WELL_PAVED("노면이 좋은"),
    TRAIL("흙길"),
    BEGINNER_FRIENDLY("초보자에게 좋은"),
    LONG_DISTANCE("장거리"),
    SHORT_DISTANCE("단거리");

    public static final int MAX_TAGS_PER_COURSE = 5;

    private final String label;
}
