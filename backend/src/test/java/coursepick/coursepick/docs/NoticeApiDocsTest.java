package coursepick.coursepick.docs;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import coursepick.coursepick.presentation.NoticeV1WebController;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NoticeApiDocsTest extends AbstractApiDocsSupport {

    private static final String TAG = "노티스 (Notice)";

    @Override
    Object initController() {
        return new NoticeV1WebController();
    }

    @Test
    void 공지사항_목록_조회_API() throws Exception {
        mockMvc.perform(get("/v1/notices")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("notice-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag(TAG)
                                .summary("공지사항 목록 조회")
                                .description("사용자에게 필요한 공지사항 목록을 조회하는 API입니다.")
                                .responseFields(
                                        fieldWithPath("notices")
                                                .description("공지사항 리스트"),
                                        fieldWithPath("notices[].id")
                                                .description("공지사항 ID")
                                                .type(STRING)
                                                .optional(),
                                        fieldWithPath("notices[].title")
                                                .description("공지사항 제목")
                                                .type(STRING)
                                                .optional(),
                                        fieldWithPath("notices[].description")
                                                .description("공지사항 내용")
                                                .type(STRING)
                                                .optional(),
                                        fieldWithPath("notices[].imageUrl")
                                                .description("공지사항 이미지 URL")
                                                .type(STRING)
                                                .optional(),
                                        fieldWithPath("notices[].targetUrl")
                                                .description("공지사항 타겟 URL")
                                                .type(STRING)
                                                .optional()
                                ).build())));
    }
}
