package coursepick.coursepick.infrastructure;

import coursepick.coursepick.application.JwtProvider;
import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.domain.Admin;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JJwtProvider implements JwtProvider {

    private final SecretKey secretKey;
    private final Long tokenValidMillis;

    public JJwtProvider(
            @Value("${auth.jwt.secret-key}") String secretKey,
            @Value("${auth.jwt.valid-millis}") Long tokenValidMillis
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.tokenValidMillis = tokenValidMillis;
    }

    @Override
    public String createToken(Admin admin) {
        return Jwts.builder()
                .subject(admin.id())
                .expiration(new Date(System.currentTimeMillis() + tokenValidMillis))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw ErrorType.TOKEN_EXPIRED.create();
        } catch (Exception e) {
            throw ErrorType.TOKEN_INVALID.create();
        }
    }
}
