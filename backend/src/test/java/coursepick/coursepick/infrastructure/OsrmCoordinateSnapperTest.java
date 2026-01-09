package coursepick.coursepick.infrastructure;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.infrastructure.snapper.OsrmCoordinateSnapper;
import coursepick.coursepick.test_util.AbstractMockServerTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OsrmCoordinateSnapperTest extends AbstractMockServerTest {

    @Test
    void 좌표_리스트를_도로에_스냅할_수_있다() {
        mock(osrmSnapResponse());
        var sut = new OsrmCoordinateSnapper(anyRestClient());

        List<Coordinate> originals = List.of(
                new Coordinate(37.5045224, 127.048996, 10.0),
                new Coordinate(37.5050000, 127.048500, 15.0),
                new Coordinate(37.5113001, 127.0392855, 20.0)
        );
        var result = sut.snap(originals);

        assertThat(result.coordinates()).isNotEmpty();
        assertThat(result.coordinates().size()).isEqualTo(5); // Mock 응답에 5개 좌표
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
                      "distance": 850.5,
                      "confidence": 0.85
                    }
                  ]
                }
                """;
    }

    @Test
    void 스냅된_좌표에_원본_elevation이_보간된다() {
        mock(osrmSnapResponse());
        var sut = new OsrmCoordinateSnapper(anyRestClient());

        List<Coordinate> originals = List.of(
                new Coordinate(37.5045224, 127.048996, 10.0),
                new Coordinate(37.5050000, 127.048500, 15.0),
                new Coordinate(37.5113001, 127.0392855, 20.0)
        );
        var result = sut.snap(originals);

        assertThat(result.coordinates()).allMatch(coord -> coord.elevation() != 0.0);
        assertThat(result.coordinates().get(0).elevation()).isBetween(9.0, 11.0);
        assertThat(result.coordinates().get(result.coordinates().size() - 1).elevation()).isBetween(15.0, 21.0);
    }

    @Test
    void 좌표가_2개_미만이면_원본을_반환한다() {
        var sut = new OsrmCoordinateSnapper(anyRestClient());

        List<Coordinate> single = List.of(
                new Coordinate(37.5045224, 127.048996, 10.0)
        );
        var result = sut.snap(single);

        assertThat(result.coordinates()).isEqualTo(single);
    }

    @Test
    void 빈_리스트는_빈_리스트를_반환한다() {
        var sut = new OsrmCoordinateSnapper(anyRestClient());

        var result = sut.snap(List.of());

        assertThat(result.coordinates()).isEmpty();
    }

    @Test
    void 응답이_오래걸리면_원본_좌표를_반환한다() {
        mock(osrmSnapResponse(), 6000);
        var sut = new OsrmCoordinateSnapper(anyRestClient());

        List<Coordinate> originals = List.of(
                new Coordinate(37.5045224, 127.048996, 10.0),
                new Coordinate(37.5113001, 127.0392855, 20.0)
        );

        var result = sut.snap(originals);
        assertThat(result.coordinates()).isEqualTo(originals);
    }

    @Test
    void NoMatch_응답이면_원본_좌표를_반환한다() {
        mock(osrmNoMatchResponse());
        var sut = new OsrmCoordinateSnapper(anyRestClient());

        List<Coordinate> originals = List.of(
                new Coordinate(37.5045224, 127.048996, 10.0),
                new Coordinate(37.5113001, 127.0392855, 20.0)
        );
        var result = sut.snap(originals);

        assertThat(result.coordinates()).isEqualTo(originals);
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
                new Coordinate(37.5045224, 127.048996, 10.0),
                new Coordinate(37.5113001, 127.0392855, 20.0)
        );
        var result = sut.snap(originals);

        assertThat(result.coordinates()).isEqualTo(originals);
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
