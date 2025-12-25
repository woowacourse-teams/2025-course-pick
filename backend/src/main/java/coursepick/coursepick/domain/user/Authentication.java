package coursepick.coursepick.domain.user;

import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public record Authentication(
        String accessToken
) {
    private static final Duration TOKEN_VALIDITY = Duration.ofDays(30);

    public static Authentication auth(SecretKey key, User user) {
        String accessToken = createAccessToken(key, user);
        return new Authentication(accessToken);
    }

    private static String createAccessToken(SecretKey key, User user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(TOKEN_VALIDITY);

        return Jwts.builder()
                .subject(user.id())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(key)
                .compact();
    }
}
