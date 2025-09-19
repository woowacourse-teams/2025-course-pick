package coursepick.coursepick.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import coursepick.coursepick.domain.Admin;
import coursepick.coursepick.test_util.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuthApplicationServiceTest extends IntegrationTest {

    @Autowired
    AuthApplicationService sut;

    @Autowired
    PasswordHasher passwordHasher;

    @Test
    void 존재하지_않는_어드민_계정인_경우_예외가_발생한다() {
        assertThatThrownBy(() -> sut.validateAndCreateToken("NoId", "password"))
                .isInstanceOf(SecurityException.class)
                .hasMessage("로그인에 실패했습니다.");
    }

    @Test
    void 잘못된_비밀번호일_경우_예외가_발생한다() {
        Admin admin = dbUtil.saveAdmin(new Admin("adminId", "password"));

        assertThatThrownBy(() -> sut.validateAndCreateToken(admin.username(), "invalidPassword"))
                .isInstanceOf(SecurityException.class)
                .hasMessage("로그인에 실패했습니다.");
    }

    @Test
    void 로그인_검증에_성공하면_토큰을_리턴한다() {
        var username = "adminId";
        var rawPassword = "password";
        var encodedPassword = passwordHasher.hash(rawPassword);
        dbUtil.saveAdmin(new Admin("adminId", encodedPassword));

        String token = sut.validateAndCreateToken(username, rawPassword);

        assertThat(token).isNotNull();
    }
}
