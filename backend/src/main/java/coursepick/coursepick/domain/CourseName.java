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
        value = compact(value);
        validateLength(value);
    }

    private static String compact(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }

    private static void validateLength(String compactName) {
        if (compactName.length() < 2 || compactName.length() > 30) {
            throw INVALID_NAME_LENGTH.create(compactName);
        }
    }
}
