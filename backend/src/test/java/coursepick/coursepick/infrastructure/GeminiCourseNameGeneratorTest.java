package coursepick.coursepick.infrastructure;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.infrastructure.ai.GeminiCourseNameGenerator;
import coursepick.coursepick.infrastructure.geocoding.ReverseGeocoder;
import coursepick.coursepick.test_util.AbstractMockServerTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeminiCourseNameGeneratorTest extends AbstractMockServerTest {

    private static final ReverseGeocoder STUB_GEOCODER = coordinate -> Optional.of("서초구 반포4동");
    private static final List<Coordinate> 코스_좌표 = List.of(
            new Coordinate(37.5087, 126.9876),
            new Coordinate(37.5100, 126.9900),
            new Coordinate(37.5087, 126.9876)
    );

    @Test
    void 좌표로_코스_이름을_생성한다() {
        mock(geminiResponse("반포4동 한바퀴"));
        var sut = new GeminiCourseNameGenerator(anyRestClient(), STUB_GEOCODER, "test-key");

        var result = sut.generate(코스_좌표);

        assertThat(result).isEqualTo("반포4동 한바퀴");
    }

    @Test
    void 응답에_섞인_따옴표와_확장자를_제거한다() {
        mock(geminiResponse("\\\"반포4동 러닝코스.gpx\\\""));
        var sut = new GeminiCourseNameGenerator(anyRestClient(), STUB_GEOCODER, "test-key");

        var result = sut.generate(코스_좌표);

        assertThat(result).isEqualTo("반포4동 러닝코스");
    }

    @Test
    void 생성에_실패하면_예외가_발생한다() {
        mock("올바르지 않은 응답");
        var sut = new GeminiCourseNameGenerator(anyRestClient(), STUB_GEOCODER, "test-key");

        assertThatThrownBy(() -> sut.generate(코스_좌표))
                .isInstanceOf(IllegalStateException.class);
    }

    private static String geminiResponse(String text) {
        return """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          { "text": "%s" }
                        ]
                      }
                    }
                  ]
                }
                """.formatted(text);
    }
}
