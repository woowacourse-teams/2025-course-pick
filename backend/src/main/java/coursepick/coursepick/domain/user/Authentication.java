package coursepick.coursepick.domain.user;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static coursepick.coursepick.application.exception.ErrorType.AUTHENTICATION_FAIL;

public record Authentication(
        String userId,
        String accessToken
) {
    private static final Duration TOKEN_VALIDITY = Duration.ofDays(30);

    public static Authentication auth(SecretKey key, User user) {
        String accessToken = createAccessToken(key, user);
        return new Authentication(user.id(), accessToken);
    }

    public static Authentication parse(SecretKey key, String accessToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
            return new Authentication(claims.getSubject(), accessToken);
        } catch (JwtException e) {
            throw AUTHENTICATION_FAIL.create();
        }
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
