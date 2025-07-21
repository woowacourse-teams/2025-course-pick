package coursepick.coursepick.presentation.api;

import coursepick.coursepick.presentation.dto.GeoJson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "코스")
public interface CourseWebApi {

    @Operation(summary = "좌표 근처 1km 내 코스 전체 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(
                            name = "위도가 범위 외인 경우",
                            ref = "#/components/examples/INVALID_LATITUDE_RANGE"
                    ),
                    @ExampleObject(
                            name = "경도가 범위 외인 경우",
                            ref = "#/components/examples/INVALID_LONGITUDE_RANGE"
                    )
            })),
    })
    List<GeoJson> findNearbyCourses(
            @Parameter(example = "37.5165004", required = true) double latitude,
            @Parameter(example = "127.1040109", required = true) double longitude
    );
}
