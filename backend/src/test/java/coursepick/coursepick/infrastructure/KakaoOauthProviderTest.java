package coursepick.coursepick.infrastructure;

import coursepick.coursepick.test_util.AbstractMockServerTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KakaoOauthProviderTest extends AbstractMockServerTest {

    @Test
    void providerId를_받아온다() {
        mock("""
                {
                    "id":123456789,
                    "connected_at": "2022-04-11T01:45:28Z"
                }
                """);
        var sut = new KakaoOauthProvider(anyRestClient());

        var result = sut.getProviderId("accessToken");

        assertThat(result).isEqualTo("123456789");
    }
}
