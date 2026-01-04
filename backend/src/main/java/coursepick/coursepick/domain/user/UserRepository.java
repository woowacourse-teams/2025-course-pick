package coursepick.coursepick.domain.user;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserRepository extends Repository<User, String> {
    User save(User user);

    Optional<User> findByProviderAndProviderId(UserProvider provider, String providerId);
}
