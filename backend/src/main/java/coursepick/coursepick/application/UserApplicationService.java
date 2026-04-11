package coursepick.coursepick.application;

import static coursepick.coursepick.application.exception.ErrorType.NOT_EXIST_USER;

import coursepick.coursepick.domain.user.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class UserApplicationService {

    private final UserRepository userRepository;
    private final OAuthProvider oauthProvider;
    private final NicknameGenerator nicknameGenerator;
    private final SecretKey secretKey;

    public UserApplicationService(UserRepository userRepository, OAuthProvider oauthProvider, NicknameGenerator nicknameGenerator, @Value("${jwt.secret-key}") String secretKeyString) {
        this.userRepository = userRepository;
        this.oauthProvider = oauthProvider;
        this.nicknameGenerator = nicknameGenerator;
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    @Transactional
    public Authentication registerOrLoginAndGetAuthentication(String oauthAccessToken) {
        return registerOrLoginAndGetAuthentication(UserProvider.KAKAO, oauthAccessToken);
    }

    @Transactional(readOnly = true)
    public User findUser(String userId){
        return userRepository.findById(userId).
                orElseThrow(() -> NOT_EXIST_USER.create(userId));
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
        User user = new User(userProvider, providerId, nicknameGenerator.generate());
        return userRepository.save(user);
    }
}
