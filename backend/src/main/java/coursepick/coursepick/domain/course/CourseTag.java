package coursepick.coursepick.domain.course;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum CourseTag {

    // 풍경/뷰
    NIGHT_VIEW("야경이 좋은"),
    OCEAN_VIEW("바다 뷰"),
    MOUNTAIN_VIEW("산 뷰"),
    LAKE_VIEW("호수 뷰"),
    CITY_VIEW("도시 뷰"),
    CHERRY_BLOSSOM("벚꽃 명소"),
    AUTUMN_FOLIAGE("단풍 명소"),
    SCENIC("전반적으로 풍경이 좋은"),

    // 환경/지형
    RIVERSIDE("강변"),
    PARK("공원"),
    FOREST("숲길"),
    URBAN("도심"),
    BRIDGE("다리를 지나는"),
    TRACK("트랙/운동장"),

    // 노면
    WELL_PAVED("노면이 잘 정비된"),
    RUBBER_PAVEMENT("우레탄/탄성포장"),
    TRAIL("흙길"),
    GRAVEL("자갈길"),
    STAIRS("계단이 있는"),

    // 난이도/거리
    FLAT("평탄한"),
    HILLY("언덕이 있는"),
    BEGINNER_FRIENDLY("초보자에게 좋은"),
    ADVANCED("고급자용"),
    SHORT_DISTANCE("단거리"),
    MEDIUM_DISTANCE("중거리"),
    LONG_DISTANCE("장거리"),
    LOOP("순환 코스"),

    // 분위기/조건
    QUIET("한적한"),
    CROWDED("사람이 많은"),
    SHADY("그늘이 많은"),
    WELL_LIT("야간 조명이 잘 된"),
    BIKE_SEPARATED("자전거 도로와 분리된"),

    // 대상자
    FAMILY_FRIENDLY("가족 친화적인"),
    DOG_FRIENDLY("반려견과 함께"),

    // 편의
    AMENITIES_NEARBY("화장실/식수대 가까운");

    public static final int MAX_TAGS_PER_COURSE = 5;

    private final String label;
}
