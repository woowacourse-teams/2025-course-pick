package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.CoursesResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CoursesWebResponse(
        List<CourseWebResponse> courses,
        @Schema(example = "true")
        boolean hasNext
) {
    public static CoursesWebResponse from(CoursesResponse coursesResponse) {
        return new CoursesWebResponse(
                CourseWebResponse.from(coursesResponse.courses()),
                coursesResponse.hasNext()
        );
    }
}
