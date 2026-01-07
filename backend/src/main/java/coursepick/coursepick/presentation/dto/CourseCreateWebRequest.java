package coursepick.coursepick.presentation.dto;

import java.util.List;

public record CourseCreateWebRequest(
        String name,
        String roadType,
        String difficulty,
        List<CoordinateDto> coordinates
) {
    public record CoordinateDto(
            double latitude,
            double longitude
    ) {
    }
}
