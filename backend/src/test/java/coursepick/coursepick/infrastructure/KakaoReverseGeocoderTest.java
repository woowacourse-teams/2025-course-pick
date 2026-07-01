package coursepick.coursepick.infrastructure;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.infrastructure.geocoding.KakaoReverseGeocoder;
import coursepick.coursepick.test_util.AbstractMockServerTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KakaoReverseGeocoderTest extends AbstractMockServerTest {

    @Test
    void 좌표를_행정동_이름으로_변환한다() {
        mock(coord2regioncodeResponse());
        var sut = new KakaoReverseGeocoder(anyRestClient(), "test-key");

        var result = sut.findRegionName(new Coordinate(37.5087, 126.9876));

        assertThat(result).contains("서초구 반포4동");
    }

    @Test
    void 변환_결과가_없으면_빈_값을_반환한다() {
        mock("""
                {
                  "meta": {"total_count": 0},
                  "documents": []
                }
                """);
        var sut = new KakaoReverseGeocoder(anyRestClient(), "test-key");

        var result = sut.findRegionName(new Coordinate(0, 0));

        assertThat(result).isEmpty();
    }

    @Test
    void 요청에_실패하면_빈_값을_반환한다() {
        mock("올바르지 않은 응답");
        var sut = new KakaoReverseGeocoder(anyRestClient(), "test-key");

        var result = sut.findRegionName(new Coordinate(37.5087, 126.9876));

        assertThat(result).isEmpty();
    }

    private static String coord2regioncodeResponse() {
        return """
                {
                  "meta": {"total_count": 2},
                  "documents": [
                    {
                      "region_type": "B",
                      "code": "1165010700",
                      "address_name": "서울특별시 서초구 반포동",
                      "region_1depth_name": "서울특별시",
                      "region_2depth_name": "서초구",
                      "region_3depth_name": "반포동",
                      "region_4depth_name": "",
                      "x": 126.9876,
                      "y": 37.5087
                    },
                    {
                      "region_type": "H",
                      "code": "1165056000",
                      "address_name": "서울특별시 서초구 반포4동",
                      "region_1depth_name": "서울특별시",
                      "region_2depth_name": "서초구",
                      "region_3depth_name": "반포4동",
                      "region_4depth_name": "",
                      "x": 126.9876,
                      "y": 37.5087
                    }
                  ]
                }
                """;
    }
}
