package coursepick.coursepick.application;

import coursepick.coursepick.domain.user.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class UserApplicationService {

    private final UserRepository userRepository;
    private final OAuthProvider oauthProvider;
    private final SecretKey secretKey;

    public UserApplicationService(UserRepository userRepository, OAuthProvider oauthProvider, @Value("${jwt.secret-key}") String secretKeyString) {
        this.userRepository = userRepository;
        this.oauthProvider = oauthProvider;
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    @Transactional
    public Authentication registerOrLoginAndGetAuthentication(String oauthAccessToken) {
        return registerOrLoginAndGetAuthentication(UserProvider.KAKAO, oauthAccessToken);
    }

    private Authentication registerOrLoginAndGetAuthentication(UserProvider userProvider, String oauthAccessToken) {
        String providerId = oauthProvider.getProviderId(oauthAccessToken);
        Optional<User> user = userRepository.findByProviderAndProviderId(userProvider, providerId);

        if (user.isPresent()) {
            return Authentication.auth(secretKey, user.get());
        }

        User registeredUser = register(userProvider, providerId);
        return Authentication.auth(secretKey, registeredUser);
    }

    private User register(UserProvider userProvider, String providerId) {
        User user = new User(userProvider, providerId);
        return userRepository.save(user);
    }
}
