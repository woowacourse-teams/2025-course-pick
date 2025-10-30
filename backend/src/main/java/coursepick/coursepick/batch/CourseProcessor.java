package coursepick.coursepick.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseProcessor implements ItemProcessor<Course, Course> {

    private final ObjectMapper objectMapper;
    private final CourseRepository courseRepository;

    @Override
    public Course process(Course item) {
        if (item.id() == null) {
            return item;
        }

        Optional<Course> findCourse = courseRepository.findById(item.id());
        if (findCourse.isEmpty()) {
            return item;
        }
        Course course = findCourse.get();

        String courseHash = hash(course);
        String itemHash = hash(item);

        if (courseHash.equals(itemHash)) {
            return null;
        }

        return item;
    }

    private byte[] serializeCourse(Course course) {
        try {
            return objectMapper.writeValueAsString(course).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("코스 직렬화에 실패했습니다", e);
        }
    }

    private String hash(Course course) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = serializeCourse(course);
            byte[] digest = md.digest(bytes);

            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("해시 계산에 실패했습니다.", e);
        }
    }
}
