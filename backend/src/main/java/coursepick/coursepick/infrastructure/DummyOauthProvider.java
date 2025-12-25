package coursepick.coursepick.infrastructure;

import coursepick.coursepick.domain.user.OAuthProvider;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

@Component
@Fallback
public class DummyOauthProvider implements OAuthProvider {

    @Override
    public String getProviderId(String oauthAccessToken) {
        return "authentication-complete";
    }
}
