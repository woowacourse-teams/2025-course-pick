package coursepick.coursepick.domain.user;

public record Authentication(
        String accessToken
) {
    public static Authentication auth(User user) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
