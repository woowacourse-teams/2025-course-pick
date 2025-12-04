package coursepick.coursepick.infrastructure;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.test_util.AbstractMockServerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class OsrmCoordinatesMatchServiceTest extends AbstractMockServerTest {

    RestClient osrmRestClient;

    @BeforeEach
    void setup() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(1));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        this.osrmRestClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(url())
                .build();
    }

    @Test
    void 좌표_리스트를_도로에_매칭할_수_있다() {
        mock(osrmMatchResponse());
        var sut = new OsrmCoordinatesMatchService(osrmRestClient);

        List<Coordinate> originals = List.of(
                new Coordinate(37.5045224, 127.048996, 10.0),
                new Coordinate(37.5050000, 127.048500, 15.0),
                new Coordinate(37.5113001, 127.0392855, 20.0)
        );
        var result = sut.snapCoordinates(originals);

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(5); // Mock 응답에 5개 좌표
    }

    @Test
    void 매칭된_좌표에_원본_elevation이_보간된다() {
        mock(osrmMatchResponse());
        var sut = new OsrmCoordinatesMatchService(osrmRestClient);

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
        var sut = new OsrmCoordinatesMatchService(osrmRestClient);

        List<Coordinate> single = List.of(
                new Coordinate(37.5045224, 127.048996, 10.0)
        );
        var result = sut.snapCoordinates(single);

        assertThat(result).isEqualTo(single);
    }

    @Test
    void 빈_리스트는_빈_리스트를_반환한다() {
        var sut = new OsrmCoordinatesMatchService(osrmRestClient);

        var result = sut.snapCoordinates(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void 응답이_오래걸리면_원본_좌표를_반환한다() {
        mock(osrmMatchResponse(), 6000);
        var sut = new OsrmCoordinatesMatchService(osrmRestClient);

        List<Coordinate> originals = List.of(
                new Coordinate(37.5045224, 127.048996, 10.0),
                new Coordinate(37.5113001, 127.0392855, 20.0)
        );

        var result = sut.snapCoordinates(originals);
        assertThat(result).isEqualTo(originals);
    }

    @Test
    void NoMatch_응답이면_원본_좌표를_반환한다() {
        mock(osrmNoMatchResponse());
        var sut = new OsrmCoordinatesMatchService(osrmRestClient);

        List<Coordinate> originals = List.of(
                new Coordinate(37.5045224, 127.048996, 10.0),
                new Coordinate(37.5113001, 127.0392855, 20.0)
        );
        var result = sut.snapCoordinates(originals);

        assertThat(result).isEqualTo(originals);
    }

    @Test
    void TooBig_응답이면_원본_좌표를_반환한다() {
        mock(osrmTooBigResponse());
        var sut = new OsrmCoordinatesMatchService(osrmRestClient);

        List<Coordinate> originals = List.of(
                new Coordinate(37.5045224, 127.048996, 10.0),
                new Coordinate(37.5113001, 127.0392855, 20.0)
        );
        var result = sut.snapCoordinates(originals);

        assertThat(result).isEqualTo(originals);
    }

    @Test
    void 좌표가2개_미만이면_원본_좌표를_반환한다() {
        mock(osrmLeastTwoCoordinatesResponse());
        var sut = new OsrmCoordinatesMatchService(osrmRestClient);

        List<Coordinate> originals = List.of(
                new Coordinate(37.5045224, 127.048996, 10.0)
        );
        var result = sut.snapCoordinates(originals);

        assertThat(result).isEqualTo(originals);
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

    private static String osrmNoMatchResponse() {
        return """
                  {
                    "code": "NoMatch",
                    "message": "Could not match route"
                  }
                  """;
    }

    private static String osrmTooBigResponse() {
        return """
                  {
                    "message": "Too many trace coordinates",
                    "code": "TooBig"
                  }
                  """;
    }

    private static String osrmLeastTwoCoordinatesResponse() {
        return """
                  {
                    "message":"Number of coordinates needs to be at least two.",
                    "code":"InvalidOptions"
                  }
                  """;
    }
}
