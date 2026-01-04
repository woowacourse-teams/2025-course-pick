package coursepick.coursepick.presentation.dto;

import java.util.List;

public record SnapWebRequest(
        List<CoordinateDto> coordinates
) {
    public record CoordinateDto(
            double latitude,
            double longitude
    ) {
    }
}
