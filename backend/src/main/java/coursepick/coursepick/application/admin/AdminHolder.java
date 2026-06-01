package coursepick.coursepick.application.admin;

import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserProvider;
import coursepick.coursepick.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdminHolder {
    private final UserRepository userRepository;

    public User getAdminId() {
        return userRepository.findByProviderAndProviderId(UserProvider.NONE, "admin")
                .orElseThrow(() -> ErrorType.NOT_EXIST_USER.create("admin"));
    }
}
