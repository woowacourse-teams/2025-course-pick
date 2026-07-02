package coursepick.coursepick.infrastructure.ai;

import coursepick.coursepick.domain.course.Coordinate;
import coursepick.coursepick.domain.course.CourseNameGenerator;
import coursepick.coursepick.domain.course.GeoLine;
import coursepick.coursepick.domain.course.Meter;
import coursepick.coursepick.infrastructure.geocoding.ReverseGeocoder;
import coursepick.coursepick.logging.LogContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Profile({"dev", "prod"})
public class GeminiCourseNameGenerator implements CourseNameGenerator {

    private static final String MODEL = "gemini-2.5-flash";
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 30;
    private static final Meter LOOP_THRESHOLD = new Meter(100);

    private final RestClient geminiRestClient;
    private final ReverseGeocoder reverseGeocoder;
    private final String apiKey;

    public GeminiCourseNameGenerator(
            RestClient geminiRestClient,
            ReverseGeocoder reverseGeocoder,
            @Value("${gemini.api-key}") String apiKey
    ) {
        this.geminiRestClient = geminiRestClient;
        this.reverseGeocoder = reverseGeocoder;
        this.apiKey = apiKey;
    }

    @Override
    public String generate(List<Coordinate> coordinates) {
        try {
            String prompt = buildPrompt(coordinates);
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
            );

            Map<String, Object> response = geminiRestClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/{model}:generateContent")
                            .queryParam("key", apiKey)
                            .build(MODEL))
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            return sanitize(extractText(response));
        } catch (Exception e) {
            log.warn("[EXCEPTION] Gemini 코스 이름 생성 실패", LogContent.exception(e));
            throw new IllegalStateException("AI 코스 이름 생성에 실패했습니다.", e);
        }
    }

    private String buildPrompt(List<Coordinate> coordinates) {
        Coordinate start = coordinates.getFirst();
        Coordinate end = coordinates.getLast();
        String startRegion = reverseGeocoder.findRegionName(start).orElse("알 수 없음");
        String endRegion = reverseGeocoder.findRegionName(end).orElse("알 수 없음");
        boolean loop = GeoLine.between(start, end).length().isWithin(LOOP_THRESHOLD);

        return """
                당신은 러닝 코스 이름을 짓는 전문가입니다.
                아래 정보를 바탕으로 자연스러운 한국어 러닝 코스 이름을 하나만 지어주세요.

                규칙:
                - 코스 이름만 출력하세요. 따옴표, 설명, 마크다운, 파일 확장자(.gpx 등)는 절대 포함하지 마세요.
                - %d자 이상 %d자 이내로 간결하게 지어주세요.
                - 지역/장소 이름을 적극적으로 활용하세요.
                - 출발지와 도착지가 같으면 "○○ 한바퀴"처럼, 다르면 "○○-○○"처럼 지을 수 있습니다.
                - 지역을 알 수 없으면 거리감이 느껴지는 무난한 이름을 지어주세요.

                예시: 반포4동, 발산역-화곡역, 보라매공원 한바퀴, 북한산 트레일

                출발 지역: %s
                도착 지역: %s
                순환 코스 여부: %s
                전체 거리: 약 %.1fkm
                """.formatted(
                MIN_NAME_LENGTH,
                MAX_NAME_LENGTH,
                startRegion,
                endRegion,
                loop ? "예" : "아니오",
                GeoLine.totalLength(coordinates).toKilometers()
        );
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalStateException("Gemini 응답에 candidates가 없습니다: " + response);
        }
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = content == null ? null : (List<Map<String, Object>>) content.get("parts");
        if (parts == null || parts.isEmpty()) {
            throw new IllegalStateException("Gemini 응답에 parts가 없습니다: " + response);
        }
        String text = (String) parts.get(0).get("text");
        if (text == null) {
            throw new IllegalStateException("Gemini 응답에 text가 없습니다: " + response);
        }
        return text.strip();
    }

    private String sanitize(String text) {
        String name = text.strip();
        int newlineIndex = name.indexOf('\n');
        if (newlineIndex >= 0) {
            name = name.substring(0, newlineIndex).strip();
        }
        name = name.replaceAll("^[\"'`]+|[\"'`]+$", "").strip();
        name = name.replaceAll("(?i)\\.(gpx|kml)$", "").strip();
        if (name.length() < MIN_NAME_LENGTH) {
            throw new IllegalStateException("AI가 유효한 코스 이름을 반환하지 않았습니다: " + text);
        }
        return name;
    }
}
