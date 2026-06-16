package coursepick.coursepick.domain.user;

import coursepick.coursepick.application.exception.ErrorType;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends Repository<User, String> {

    User save(User user);

    List<User> findAllByNicknameIsNull();

    Optional<User> findById(String userId);

    Optional<User> findByProviderAndProviderId(UserProvider provider, String providerId);

    default User getAdmin() {
        return findByProviderAndProviderId(UserProvider.NONE, "admin")
                .orElseThrow(() -> ErrorType.NOT_EXIST_USER.create("admin"));
    }
}
