package coursepick.coursepick.infrastructure;

import org.junit.jupiter.api.Test;

class SecurityPasswordHasherTest {

    @Test
    void 테스트() {
        SecurityPasswordHasher securityPasswordEncoder = new SecurityPasswordHasher();
        String password = securityPasswordEncoder.hash("topsecret");
        System.out.println(password);
    }

}
