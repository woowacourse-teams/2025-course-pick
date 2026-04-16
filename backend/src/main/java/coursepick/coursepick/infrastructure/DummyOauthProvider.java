package coursepick.coursepick.infrastructure;

import java.util.UUID;

import coursepick.coursepick.domain.user.OAuthProvider;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

@Component
@Fallback
public class DummyOauthProvider implements OAuthProvider {

    @Override
    public String getProviderId(String oauthAccessToken) {
        return UUID.randomUUID().toString();
    }
}
