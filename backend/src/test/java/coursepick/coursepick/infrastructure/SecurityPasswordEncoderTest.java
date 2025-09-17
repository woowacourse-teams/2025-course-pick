package coursepick.coursepick.infrastructure;

import coursepick.coursepick.application.PasswordEncoder;
import org.junit.jupiter.api.Test;

class SecurityPasswordEncoderTest {

    @Test
    void 비밀번호_해시() {
        PasswordEncoder passwordEncoder = new SecurityPasswordEncoder();
        String password = passwordEncoder.encode("password");
        System.out.println(password);
    }
}
