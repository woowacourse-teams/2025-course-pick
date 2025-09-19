package coursepick.coursepick.application;

public interface PasswordHasher {

    String hash(String password);

    boolean matches(String rawPassword, String encodedPassword);
}
