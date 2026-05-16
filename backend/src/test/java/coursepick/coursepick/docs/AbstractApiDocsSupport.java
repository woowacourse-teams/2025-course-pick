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
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@WebMvcTest(controllers = {CourseV1WebController.class, UserV1WebController.class, NoticeV1WebController.class})
@Import({WebConfig.class, UserIdArgumentResolver.class})
@ExtendWith(RestDocumentationExtension.class)
public abstract class AbstractApiDocsSupport {

    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected CourseApplicationService courseApplicationService;

    @MockitoBean
    protected UserApplicationService userApplicationService;

    @MockitoBean
    protected LoginInterceptor loginInterceptor;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) throws Exception {

        this.mockMvc = MockMvcBuilders.standaloneSetup(initController())
                .setControllerAdvice(new WebExceptionHandler())
                .setCustomArgumentResolvers(new UserIdArgumentResolver())
                .apply(documentationConfiguration(restDocumentation))
                .build();

        given(loginInterceptor.preHandle(any(), any(), any())).willAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            request.setAttribute("AUTH_USER_ID", "test-user-id");
            return true;
        });
    }

    protected abstract Object initController();
}
