package coursepick.coursepick.batch;

import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseProcessor implements ItemProcessor<Course, Course> {

    private final CourseRepository courseRepository;

    @Override
    public Course process(Course item) {
        boolean exists = courseRepository.existsByName(item.name());
        return exists ? null : item;
    }

    public Course process2(Course item) {
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
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(course);
            oos.flush();

            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private String hash(Course course) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = serializeCourse(course);
            byte[] digest = md.digest(bytes);

            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException();
        }
    }
}
