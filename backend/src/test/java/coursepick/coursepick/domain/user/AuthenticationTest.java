package coursepick.coursepick.domain.user;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationTest {

    private static final Duration TOKEN_VALIDITY = Duration.ofDays(30);
    private static final SecretKey TEST_KEY = Jwts.SIG.HS256.key().build();
    private static final User TEST_USER = new User("user123", UserProvider.KAKAO, "kakaoUserId123");

    @Test
    void 생성된_토큰은_사용자_ID를_subject로_포함한다() {
        Authentication authentication = Authentication.auth(TEST_KEY, TEST_USER);

        Claims claims = Jwts.parser()
                .verifyWith(TEST_KEY)
                .build()
                .parseSignedClaims(authentication.accessToken())
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("user123");
    }

    @Test
    void 생성된_토큰은_30일_유효기간을_가진다() {
        Instant beforeCreation = Instant.now().minusSeconds(1);

        Authentication authentication = Authentication.auth(TEST_KEY, TEST_USER);

        Instant afterCreation = Instant.now().plusSeconds(1);
        Claims claims = Jwts.parser()
                .verifyWith(TEST_KEY)
                .build()
                .parseSignedClaims(authentication.accessToken())
                .getPayload();

        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();

        assertThat(issuedAt).isBetween(
                Date.from(beforeCreation),
                Date.from(afterCreation)
        );

        long actualValiditySeconds = (expiration.getTime() - issuedAt.getTime()) / 1000;
        long expectedValiditySeconds = TOKEN_VALIDITY.toSeconds();

        assertThat(actualValiditySeconds).isEqualTo(expectedValiditySeconds);
    }

    @Test
    void 생성된_토큰은_올바른_키로_서명된다() {
        Authentication authentication = Authentication.auth(TEST_KEY, TEST_USER);

        assertThat(authentication.accessToken()).isNotNull();

        Claims claims = Jwts.parser()
                .verifyWith(TEST_KEY)
                .build()
                .parseSignedClaims(authentication.accessToken())
                .getPayload();

        assertThat(claims).isNotNull();
    }
}
