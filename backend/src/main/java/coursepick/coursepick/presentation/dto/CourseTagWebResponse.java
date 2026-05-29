package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.domain.course.CourseTag;

import java.util.List;

public record CourseTagWebResponse(
        String name,
        String label
) {
    public static List<CourseTagWebResponse> from(List<CourseTag> tags) {
        if (tags == null) return List.of();
        return tags.stream()
                .map(tag -> new CourseTagWebResponse(tag.name(), tag.label()))
                .toList();
    }
}
