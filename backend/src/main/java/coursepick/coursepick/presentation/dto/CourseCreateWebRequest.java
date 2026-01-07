package coursepick.coursepick.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CourseCreateWebRequest(
        String name,
        String roadType,
        String difficulty,
        @NotNull List<CoordinateWebRequest> coordinates
) {
}
