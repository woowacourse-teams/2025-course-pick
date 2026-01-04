package coursepick.coursepick.infrastructure;

import coursepick.coursepick.domain.user.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@Component
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class KakaoOauthProvider implements OAuthProvider {

    private final RestClient kakaoRestClient;

    @Override
    public String getProviderId(String oauthAccessToken) {
        return kakaoRestClient.get()
                .uri("/v2/user/me")
                .header(AUTHORIZATION, "Bearer " + oauthAccessToken)
                .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8")
                .retrieve()
                .body(KakaoUserInfoResponse.class)
                .id();
    }

    private record KakaoUserInfoResponse(
            String id
    ) {
    }
}
