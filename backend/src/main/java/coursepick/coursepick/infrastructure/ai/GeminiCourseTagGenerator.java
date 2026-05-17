package coursepick.coursepick.infrastructure.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import coursepick.coursepick.domain.course.Course;
import coursepick.coursepick.domain.course.CourseTag;
import coursepick.coursepick.domain.course.CourseTagGenerator;
import coursepick.coursepick.domain.course.Review;
import coursepick.coursepick.logging.LogContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
@Profile({"dev", "prod"})
public class GeminiCourseTagGenerator implements CourseTagGenerator {

    private static final int MAX_REVIEW_SAMPLE = 10;
    private static final String MODEL = "gemini-2.5-flash";
    private static final Pattern JSON_ARRAY_PATTERN = Pattern.compile("\\[[^\\[\\]]*]", Pattern.DOTALL);

    private final RestClient geminiRestClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public GeminiCourseTagGenerator(
            RestClient geminiRestClient,
            ObjectMapper objectMapper,
            @Value("${gemini.api-key}") String apiKey
    ) {
        this.geminiRestClient = geminiRestClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    @Override
    public List<CourseTag> generate(Course course) {
        try {
            String prompt = buildPrompt(course);
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

            String text = extractText(response);
            return parseTags(text);
        } catch (Exception e) {
            log.warn("[EXCEPTION] Gemini 태그 생성 실패", LogContent.exception(e));
            throw new IllegalStateException("AI 태그 생성에 실패했습니다.", e);
        }
    }

    private String buildPrompt(Course course) {
        String tagCatalog = Arrays.stream(CourseTag.values())
                .map(t -> "- " + t.name() + ": " + t.label())
                .collect(Collectors.joining("\n"));

        String reviewBlock = recentReviews(course).stream()
                .map(r -> "- " + r.authorNickname() + ": " + r.content())
                .collect(Collectors.joining("\n"));

        return """
                Pick up to %d tags from the list below that best describe this running course.
                Respond with ONLY a JSON array of enum names (uppercase strings). No prose, no markdown code blocks, no explanation.
                Do not include tags that do not clearly apply, and skip tags you are not confident about.
                Course name and reviews are written in Korean; interpret them and choose appropriate English enum names.

                Tag list (NAME: Korean label):
                %s

                Course name: %s
                Total length: %.0f m
                Coordinate count: %d
                Recent reviews (up to %d):
                %s

                Example response: ["NIGHT_VIEW", "FLAT", "RIVERSIDE"]
                """.formatted(
                CourseTag.MAX_TAGS_PER_COURSE,
                tagCatalog,
                course.name().value(),
                course.length().value(),
                course.coordinates().size(),
                MAX_REVIEW_SAMPLE,
                reviewBlock
        );
    }

    private List<Review> recentReviews(Course course) {
        List<Review> reviews = course.reviews();
        int from = Math.max(0, reviews.size() - MAX_REVIEW_SAMPLE);
        return reviews.subList(from, reviews.size());
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        return ((String) parts.get(0).get("text")).strip();
    }

    private List<CourseTag> parseTags(String text) throws Exception {
        Matcher matcher = JSON_ARRAY_PATTERN.matcher(text);
        if (!matcher.find()) {
            log.warn("Gemini 응답에서 JSON 배열을 찾지 못함: {}", text);
            return List.of();
        }
        List<String> rawNames = objectMapper.readValue(matcher.group(), new TypeReference<>() {
        });

        return rawNames.stream()
                .map(this::toTagOrNull)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .limit(CourseTag.MAX_TAGS_PER_COURSE)
                .toList();
    }

    private CourseTag toTagOrNull(String name) {
        try {
            return CourseTag.valueOf(name.trim());
        } catch (IllegalArgumentException e) {
            log.warn("알 수 없는 태그 이름: {}", name);
            return null;
        }
    }
}
