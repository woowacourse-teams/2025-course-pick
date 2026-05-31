package coursepick.coursepick.docs;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import coursepick.coursepick.domain.user.Authentication;
import coursepick.coursepick.presentation.UserV1WebController;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.ResultHandler;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserApiDocsTest extends AbstractApiDocsSupport {

    private static final String TAG = "회원 (User)";

    @Override
    Object initController() {
        return new UserV1WebController(super.userApplicationService);
    }

    @Test
    void 카카오_로그인_API() throws Exception {
        given(userApplicationService.registerOrLoginAndGetAuthentication(anyString()))
                .willReturn(new Authentication("user_id_example", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token"));

        var requestBody = objectMapper.writeValueAsString(new java.util.LinkedHashMap<>() {
            {
                put("accessToken", "kakao_access_token_example");
            }
        });

        mockMvc.perform(post("/v1/login/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("user-sign",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("카카오 소셜 로그인")
                                .description("카카오 액세스 토큰으로 로그인/회원가입을 수행합니다.")
                                .requestFields(
                                        fieldWithPath("accessToken")
                                                .description("카카오 액세스토큰")
                                                .attributes(key("example")
                                                        .value("kakao_access_token_example")))
                                .responseFields(
                                        fieldWithPath("userId")
                                                .description("사용자 ID"),
                                        fieldWithPath("accessToken")
                                                .description("코스픽 엑세스토큰 (JWT)"))
                                .build())));
    }

    @Test
    void 카카오_로그인_API_400_에러() throws Exception {
        var requestBody = objectMapper.writeValueAsString(new java.util.LinkedHashMap<>() {
            {
                put("accessToken", "");
            }
        });

        mockMvc.perform(post("/v1/login/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(documentBadRequest("user-sign-400"));
    }

    private FieldDescriptor[] errorResponseFields() {
        return new FieldDescriptor[]{
                fieldWithPath("message").description("에러 상세 메시지"),
                fieldWithPath("errorCode").description("에러 코드"),
                fieldWithPath("timestamp").description("에러 발생 시각")
        };
    }

    private ResultHandler documentBadRequest(String documentId) {
        return MockMvcRestDocumentationWrapper.document(documentId,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(ResourceSnippetParameters.builder()
                        .tag(TAG)
                        .responseFields(errorResponseFields())
                        .build()));
    }
}
