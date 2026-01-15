package coursepick.coursepick.infrastructure;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.infrastructure.snapper.OsrmCoordinateSnapper;
import coursepick.coursepick.test_util.AbstractMockServerTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OsrmCoordinateSnapperTest extends AbstractMockServerTest {

    @Test
    void 좌표_리스트를_도로에_매칭할_수_있다() {
        mock(osrmSnapResponse());
        var sut = new OsrmCoordinateSnapper(anyRestClient());

        List<Coordinate> originals = List.of(
                new Coordinate(37.5045224, 127.048996),
                new Coordinate(37.5050000, 127.048500),
                new Coordinate(37.5113001, 127.0392855)
        );
        var result = sut.snap(originals);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(5); // Mock 응답에 5개 좌표
    }

    // Mock OSRM Match API 응답
    private static String osrmSnapResponse() {
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

    @Test
    void 좌표가_2개_미만이면_원본을_반환한다() {
        var sut = new OsrmCoordinateSnapper(anyRestClient());

        List<Coordinate> single = List.of(
                new Coordinate(37.5045224, 127.048996)
        );
        var result = sut.snap(single);

        assertThat(result).isEqualTo(single);
    }

    @Test
    void 빈_리스트는_빈_리스트를_반환한다() {
        var sut = new OsrmCoordinateSnapper(anyRestClient());

        var result = sut.snap(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void 응답이_오래걸리면_원본_좌표를_반환한다() {
        mock(osrmSnapResponse(), 6000);
        var sut = new OsrmCoordinateSnapper(anyRestClient());

        List<Coordinate> originals = List.of(
                new Coordinate(37.5045224, 127.048996),
                new Coordinate(37.5113001, 127.0392855)
        );

        var result = sut.snap(originals);
        assertThat(result).isEqualTo(originals);
    }

    @Test
    void NoMatch_응답이면_원본_좌표를_반환한다() {
        mock(osrmNoMatchResponse());
        var sut = new OsrmCoordinateSnapper(anyRestClient());

        List<Coordinate> originals = List.of(
                new Coordinate(37.5045224, 127.048996),
                new Coordinate(37.5113001, 127.0392855)
        );
        var result = sut.snap(originals);

        assertThat(result).isEqualTo(originals);
    }

    private static String osrmNoMatchResponse() {
        return """
                {
                  "code": "NoMatch",
                  "message": "Could not match route"
                }
                """;
    }

    @Test
    void TooBig_응답이면_원본_좌표를_반환한다() {
        mock(osrmTooBigResponse());
        var sut = new OsrmCoordinateSnapper(anyRestClient());

        List<Coordinate> originals = List.of(
                new Coordinate(37.5045224, 127.048996),
                new Coordinate(37.5113001, 127.0392855)
        );
        var result = sut.snap(originals);

        assertThat(result).isEqualTo(originals);
    }

    private static String osrmTooBigResponse() {
        return """
                {
                  "message": "Too many trace coordinates",
                  "code": "TooBig"
                }
                """;
    }
}
