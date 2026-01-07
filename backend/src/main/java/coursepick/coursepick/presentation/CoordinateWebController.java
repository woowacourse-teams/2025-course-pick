package coursepick.coursepick.presentation;

import coursepick.coursepick.application.CourseApplicationService;
import coursepick.coursepick.application.dto.SnapResponse;
import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.presentation.api.CoordinateWebApi;
import coursepick.coursepick.presentation.dto.SnapWebRequest;
import coursepick.coursepick.presentation.dto.SnapWebResponse;
import coursepick.coursepick.security.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CoordinateWebController implements CoordinateWebApi {

    private final CourseApplicationService courseApplicationService;

    @Override
    @Login
    @PostMapping("/courses/snap")
    public SnapWebResponse snapCoordinates(@RequestBody SnapWebRequest snapWebRequest) {
        List<Coordinate> coordinates = snapWebRequest.coordinates().stream()
                .map(dto -> new Coordinate(dto.latitude(), dto.longitude()))
                .toList();

        SnapResponse snapResponse = courseApplicationService.snapCoordinates(coordinates);
        return SnapWebResponse.from(snapResponse);
    }
}
