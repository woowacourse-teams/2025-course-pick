package coursepick.coursepick.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CustomCourseWebRequest(
        @NotNull
        String name,
        @NotNull
        List<List<Double>> coordinates
) {
}
