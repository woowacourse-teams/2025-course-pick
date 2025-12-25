package coursepick.coursepick.domain.user;

public interface OauthProvider {
    String getProviderId(String oauthAccessToken);
}
