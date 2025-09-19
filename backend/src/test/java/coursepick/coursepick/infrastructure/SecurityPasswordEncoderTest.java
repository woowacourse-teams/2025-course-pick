package coursepick.coursepick.infrastructure;

import org.junit.jupiter.api.Test;

class SecurityPasswordEncoderTest {

    @Test
    void 테스트() {
        SecurityPasswordEncoder securityPasswordEncoder = new SecurityPasswordEncoder();
        String password = securityPasswordEncoder.encode("topsecret");
        System.out.println(password);
    }

}
