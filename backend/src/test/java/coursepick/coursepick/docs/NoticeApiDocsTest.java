package coursepick.coursepick.docs;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import coursepick.coursepick.presentation.NoticeV1WebController;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NoticeApiDocsTest extends AbstractApiDocsSupport {

    @Override
    protected Object initController() {
        return new NoticeV1WebController("http://localhost:8080");
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
                                .tags("노티스 (Notice)", "GET")
                                .summary("공지사항 목록 조회")
                                .description("사용자에게 필요한 공지사항 목록을 조회하는 API입니다.")
                                .responseFields(
                                        fieldWithPath("notices[].id")
                                                .description("공지사항 ID"),
                                        fieldWithPath("notices[].imageUrl")
                                                .description("공지사항 이미지 URL"),
                                        fieldWithPath("notices[].title")
                                                .description("공지사항 제목"),
                                        fieldWithPath("notices[].description")
                                                .description("공지사항 내용"))
                                .build())));
    }
}
