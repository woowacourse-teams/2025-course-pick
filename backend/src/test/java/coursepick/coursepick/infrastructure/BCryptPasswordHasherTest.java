package coursepick.coursepick.infrastructure;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BCryptPasswordHasherTest {

    @Test
    void 비밀번호가_일치하면_검증에_성공한다() {
        BCryptPasswordHasher passwordHasher = new BCryptPasswordHasher();
        String password = "topSecret";
        String hashedPassword = passwordHasher.hash(password);

        boolean matches = passwordHasher.matches(password, hashedPassword);

        assertThat(matches).isTrue();
    }

    @Test
    void 비밀번호가_일치하지_않으면_검증에_실패한다() {
        BCryptPasswordHasher passwordHasher = new BCryptPasswordHasher();
        String hashedPassword = passwordHasher.hash("topSecret");

        boolean matches = passwordHasher.matches("noTopSecret", hashedPassword);

        assertThat(matches).isFalse();
    }
}
