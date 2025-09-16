package coursepick.coursepick.application;

public interface PasswordEncoder {

    String hash(String password);

    boolean matches(String rawPassword, String encodedPassword);
}
