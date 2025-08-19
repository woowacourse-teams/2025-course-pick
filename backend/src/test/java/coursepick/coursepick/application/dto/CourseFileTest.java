package coursepick.coursepick.application.dto;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.Normalizer;

import static org.assertj.core.api.Assertions.assertThat;

class CourseFileTest {

    @ParameterizedTest
    @ValueSource(strings = {"File.gpx", "File.GPX", "File.Gpx"})
    void 확장자가_이름에_포함되어_있으면_제거된다(String filename) {
        var result = new CourseFile(filename, CourseFileExtension.GPX, null);

        assertThat(result.name()).isEqualTo("File");
    }

    @ParameterizedTest
    @CsvSource({
            "코스.gpx, 2",
            "코스이.gpx, 3",
            "코스이름.gpx, 4"
    })
    void 자모가_분리되어_있어도_파일이름_길이가_정상적으로_집계된다(String filename, int expectedLength) {
        var macFileNameInput = Normalizer.normalize(filename, Normalizer.Form.NFD);

        var result = new CourseFile(macFileNameInput, CourseFileExtension.GPX, null);

        assertThat(result.name().length()).isEqualTo(expectedLength);
    }
}
