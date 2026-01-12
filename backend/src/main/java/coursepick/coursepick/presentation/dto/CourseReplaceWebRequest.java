package coursepick.coursepick.presentation.dto;

import java.util.List;

public record CourseReplaceWebRequest(
        List<List<Double>> coordinates,
        String name
) {
}
