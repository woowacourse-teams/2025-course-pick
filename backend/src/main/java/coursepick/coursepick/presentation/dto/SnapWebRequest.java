package coursepick.coursepick.presentation.dto;

import java.util.List;

public record SnapWebRequest(
        List<CoordinateWebRequest> coordinates
) {
}
