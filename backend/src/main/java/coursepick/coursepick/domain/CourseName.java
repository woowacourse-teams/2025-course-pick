package coursepick.coursepick.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_NAME_LENGTH;

@Embeddable
public record CourseName(
        @Column(name = "name", nullable = false)
        String value
) {
    public CourseName {
        String compactName = compact(value);
        validateLength(compactName);
    }

    private static String compact(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }

    private static void validateLength(String compactName) {
        if (compactName.length() < 2 || compactName.length() > 30) {
            throw new IllegalArgumentException(INVALID_NAME_LENGTH.message(compactName));
        }
    }
}
