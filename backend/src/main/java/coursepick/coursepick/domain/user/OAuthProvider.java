package coursepick.coursepick.domain.user;

public interface OAuthProvider {
    String getProviderId(String oauthAccessToken);
}
