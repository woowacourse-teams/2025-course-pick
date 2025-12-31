package coursepick.coursepick.security;

import coursepick.coursepick.test_util.AbstractSecurityTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class AuthorizationFlowTest extends AbstractSecurityTest {

    @Test
    void 정상적인_토큰으로_인증한다() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test1")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 비정상적인_토큰으로_인증하면_예외가_발생한다() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test1")
                        .header("Authorization", "Bearer " + "bad-token"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void 정상적인_토큰으로_인증하고_유저ID를_추출한다() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test2")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(userId));
    }
}
