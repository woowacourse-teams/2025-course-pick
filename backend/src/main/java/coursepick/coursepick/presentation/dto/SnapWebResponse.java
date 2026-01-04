package coursepick.coursepick.presentation.dto;

import coursepick.coursepick.application.dto.SnapResponse;

import java.util.List;

public record SnapWebResponse(
        List<CoordinateWebResponse> coordinates,
        double length
) {
    public static SnapWebResponse from(SnapResponse snapResponse) {
        List<CoordinateWebResponse> coordinateWebResponses = snapResponse.coordinates().stream()
                .map(CoordinateWebResponse::from)
                .toList();
        double length = snapResponse.length();

        return new SnapWebResponse(coordinateWebResponses, length);
    }
}
