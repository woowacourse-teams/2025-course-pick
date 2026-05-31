package coursepick.coursepick.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import coursepick.coursepick.application.CourseApplicationService;
import coursepick.coursepick.application.UserApplicationService;
import coursepick.coursepick.presentation.CourseV1WebController;
import coursepick.coursepick.presentation.NoticeV1WebController;
import coursepick.coursepick.presentation.UserV1WebController;
import coursepick.coursepick.presentation.WebExceptionHandler;
import coursepick.coursepick.security.LoginInterceptor;
import coursepick.coursepick.security.UserIdArgumentResolver;
import coursepick.coursepick.security.WebConfig;
import coursepick.coursepick.application.exception.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;


@ActiveProfiles({"dev", "local"})
@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
public abstract class AbstractApiDocsSupport {

    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    protected CourseApplicationService courseApplicationService;

    @Mock
    protected UserApplicationService userApplicationService;

    @Mock
    protected LoginInterceptor loginInterceptor;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) throws Exception {
        // 로그인 인터셉터 모킹 (MockMvc 빌드 전에 설정되어야 함)
        given(loginInterceptor.preHandle(any(), any(), any())).willAnswer(invocation -> {
            var request = (HttpServletRequest) invocation.getArgument(0);
            request.setAttribute("AUTH_USER_ID", "test-user-id");
            return true;
        });

        this.mockMvc = MockMvcBuilders.standaloneSetup(initController())
                .setControllerAdvice(new WebExceptionHandler())
                .setCustomArgumentResolvers(new UserIdArgumentResolver())
                .addInterceptors(loginInterceptor) // 위에서 모킹된 인터셉터 주입
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    abstract Object initController();

}
