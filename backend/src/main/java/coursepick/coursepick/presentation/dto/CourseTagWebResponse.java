package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.CourseTag;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CourseTagWebResponse(
        @Schema(description = "태그 식별자 (enum name)", example = "NIGHT_VIEW")
        String name,
        @Schema(description = "태그 한국어 라벨", example = "야경이 좋은")
        String label
) {
    public static List<CourseTagWebResponse> from(List<CourseTag> tags) {
        if (tags == null) return List.of();
        return tags.stream()
                .map(tag -> new CourseTagWebResponse(tag.name(), tag.label()))
                .toList();
    }
}
