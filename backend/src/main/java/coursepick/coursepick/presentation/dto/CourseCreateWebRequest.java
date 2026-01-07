package coursepick.coursepick.presentation.dto;

import java.util.List;

public record CourseCreateWebRequest(
        String name,
        String roadType,
        String difficulty,
        List<CoordinateWebRequest> coordinates
) {
}
