package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.CoursesResponse;

import java.util.List;

public record CoursesWebResponse(
        List<CourseWebResponse> courses,
        boolean hasNext
) {
    public static CoursesWebResponse from(CoursesResponse coursesResponse) {
        return new CoursesWebResponse(
                CourseWebResponse.from(coursesResponse.courses()),
                coursesResponse.hasNext()
        );
    }
}
