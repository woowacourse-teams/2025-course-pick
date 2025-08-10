package coursepick.coursepick.application.dto;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CourseFileTest {

    @ParameterizedTest
    @ValueSource(strings = {"File.gpx", "File.GPX", "File.Gpx"})
    void 확장자가_이름에_포함되어_있으면_제거된다(String filename) {
        var result = new CourseFile(filename, CourseFileExtension.GPX, null);

        assertThat(result.name()).isEqualTo("File");
    }
}
