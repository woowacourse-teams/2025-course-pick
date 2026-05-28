package coursepick.coursepick.domain.user;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static coursepick.coursepick.test_util.UserFixture.TEST_USER;
import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationTest {

    private static final Duration TOKEN_VALIDITY = Duration.ofDays(30);
    private static final SecretKey TEST_KEY = Jwts.SIG.HS256.key().build();

    @Test
    void 생성된_토큰은_사용자_ID를_subject로_포함한다() {
        var authentication = Authentication.auth(TEST_KEY, TEST_USER);

        var claims = Jwts.parser()
                .verifyWith(TEST_KEY)
                .build()
                .parseSignedClaims(authentication.accessToken())
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo(TEST_USER.id());
    }

    @Test
    void 생성된_토큰은_30일_유효기간을_가진다() {
        var beforeCreation = Instant.now().minusSeconds(1);

        var authentication = Authentication.auth(TEST_KEY, TEST_USER);

        var afterCreation = Instant.now().plusSeconds(1);
        var claims = Jwts.parser()
                .verifyWith(TEST_KEY)
                .build()
                .parseSignedClaims(authentication.accessToken())
                .getPayload();

        var issuedAt = claims.getIssuedAt();
        var expiration = claims.getExpiration();

        assertThat(issuedAt).isBetween(
                Date.from(beforeCreation),
                Date.from(afterCreation)
        );

        var actualValiditySeconds = (expiration.getTime() - issuedAt.getTime()) / 1000;
        var expectedValiditySeconds = TOKEN_VALIDITY.toSeconds();

        assertThat(actualValiditySeconds).isEqualTo(expectedValiditySeconds);
    }

    @Test
    void 생성된_토큰은_올바른_키로_서명된다() {
        var authentication = Authentication.auth(TEST_KEY, TEST_USER);

        assertThat(authentication.accessToken()).isNotNull();

        var claims = Jwts.parser()
                .verifyWith(TEST_KEY)
                .build()
                .parseSignedClaims(authentication.accessToken())
                .getPayload();

        assertThat(claims).isNotNull();
    }

    @Test
    void 토큰에서_사용자_ID를_추출한다() {
        var authentication = Authentication.auth(TEST_KEY, TEST_USER);

        var parsed = Authentication.parse(TEST_KEY, authentication.accessToken());

        assertThat(parsed.userId()).isEqualTo(TEST_USER.id());
    }
}
