package coursepick.coursepick.infrastructure;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import coursepick.coursepick.domain.Admin;
import org.junit.jupiter.api.Test;

class JJwtProviderTest {

    JJwtProvider sut = new JJwtProvider("testSecretKeyTestSecretKeyTestSecretKey", 1800000L);

    @Test
    void 토큰을_생성한다() {
        var admin = new Admin("adminId1", "password");

        var token = sut.createToken(admin);

        assertThatCode(() -> sut.validateToken(token))
                .doesNotThrowAnyException();
    }

    @Test
    void 조작된_토큰이면_예외가_발생한다() {
        var admin = new Admin("adminId1", "password");
        var modifiedToken = sut.createToken(admin) + "fakeToken";

        assertThatThrownBy(() -> sut.validateToken(modifiedToken))
                .isInstanceOf(SecurityException.class)
                .hasMessage("잘못된 토큰입니다.");
    }

    @Test
    void 만료된_토큰이면_예외가_발생한다() {
        sut = new JJwtProvider("testSecretKeyTestSecretKeyTestSecretKey", 0L);
        var admin = new Admin("adminId1", "password");
        var expiredToken = sut.createToken(admin);

        assertThatThrownBy(() -> sut.validateToken(expiredToken))
                .isInstanceOf(SecurityException.class)
                .hasMessage("만료된 토큰입니다.");
    }
}
