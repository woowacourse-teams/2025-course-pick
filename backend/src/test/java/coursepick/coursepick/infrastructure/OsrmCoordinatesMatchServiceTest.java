package coursepick.coursepick.infrastructure;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.test_util.AbstractMockServerTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OsrmCoordinatesMatchServiceTest extends AbstractMockServerTest {

    @Test
    void 좌표_리스트를_도로에_매칭할_수_있다() {
        // Given: Mock OSRM Match API 응답
        mock(osrmMatchResponse());
        var sut = new OsrmCoordinatesMatchService(osrmRestClient);

        // When: 원본 좌표 3개로 매칭 실행
        List<Coordinate> originals = List.of(
                new Coordinate(37.5045224, 127.048996, 10.0),
                new Coordinate(37.5050000, 127.048500, 15.0),
                new Coordinate(37.5113001, 127.0392855, 20.0)
        );
        var result = sut.snapCoordinates(originals);

        // Then: 매칭된 좌표가 반환됨 (도로를 따라 더 많은 좌표 생성)
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(5); // Mock 응답에 5개 좌표
    }

    @Test
    void 매칭된_좌표에_원본_elevation이_보간된다() {
        // Given
        mock(osrmMatchResponse());
        var sut = new OsrmCoordinatesMatchService(osrmRestClient);

        // When
        List<Coordinate> originals = List.of(
                new Coordinate(37.5045224, 127.048996, 10.0),
                new Coordinate(37.5050000, 127.048500, 15.0),
                new Coordinate(37.5113001, 127.0392855, 20.0)
        );
        var result = sut.snapCoordinates(originals);

        assertThat(result).allMatch(coord -> coord.elevation() != 0.0);
        assertThat(result.get(0).elevation()).isBetween(9.0, 11.0);
        assertThat(result.get(result.size() - 1).elevation()).isBetween(15.0, 21.0);
    }

    @Test
    void 좌표가_2개_미만이면_원본을_반환한다() {
        // Given
        var sut = new OsrmCoordinatesMatchService(osrmRestClient);

        // When: 좌표 1개
        List<Coordinate> single = List.of(
                new Coordinate(37.5045224, 127.048996, 10.0)
        );
        var result = sut.snapCoordinates(single);

        // Then: 원본 그대로 반환
        assertThat(result).isEqualTo(single);
    }

    @Test
    void 빈_리스트는_빈_리스트를_반환한다() {
        // Given
        var sut = new OsrmCoordinatesMatchService(osrmRestClient);

        // When
        var result = sut.snapCoordinates(List.of());

        // Then
        assertThat(result).isEmpty();
    }

    // Mock OSRM Match API 응답
    private static String osrmMatchResponse() {
        return """
                  {
                    "code": "Ok",
                    "matchings": [
                      {
                        "geometry": {
                          "coordinates": [
                            [127.04901, 37.504526],
                            [127.048307, 37.505993],
                            [127.047943, 37.506427],
                            [127.045223, 37.508171],
                            [127.039294, 37.511302]
                          ],
                          "type": "LineString"
                        },
                        "confidence": 0.85
                      }
                    ]
                  }
                  """;
    }
}
