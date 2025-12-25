package coursepick.coursepick.application;

import coursepick.coursepick.domain.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;
    private final OauthProvider oauthProvider;

    @Transactional
    public Authentication registerOrLoginAndGetAuthentication(String oauthAccessToken) {
        return registerOrLoginAndGetAuthentication(UserProvider.KAKAO, oauthAccessToken);
    }

    private Authentication registerOrLoginAndGetAuthentication(UserProvider userProvider, String oauthAccessToken) {
        String providerId = oauthProvider.getProviderId(oauthAccessToken);
        Optional<User> user = userRepository.findByProviderAndProviderId(userProvider, providerId);

        if (user.isPresent()) {
            return Authentication.auth(user.get());
        }

        User registeredUser = register(userProvider, providerId);
        return Authentication.auth(registeredUser);
    }

    private User register(UserProvider userProvider, String providerId) {
        User user = new User(userProvider, providerId);
        return userRepository.save(user);
    }
}
