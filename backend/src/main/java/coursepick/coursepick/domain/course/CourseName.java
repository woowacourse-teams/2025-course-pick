package coursepick.coursepick.domain.course;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_NAME_LENGTH;

public record CourseName(
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
