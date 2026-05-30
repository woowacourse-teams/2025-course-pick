package coursepick.coursepick.test_util;

import coursepick.coursepick.application.Alerter;
import coursepick.coursepick.domain.course.CourseTagGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static coursepick.coursepick.test_util.UserFixture.*;

@Import({GpxTestUtil.class, DatabaseTestUtil.class, SyncAsyncTestConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class AbstractIntegrationTest {

    @Autowired
    protected DatabaseTestUtil dbUtil;

    @MockitoBean
    protected Alerter alerter;

    @MockitoBean
    protected CourseTagGenerator courseTagGenerator;

    @BeforeEach
    void setUp() {
        dbUtil.saveUser(ADMIN_USER);
        dbUtil.saveUser(TEST_USER);
        dbUtil.saveUser(TEST_USER2);
        dbUtil.saveUser(TEST_USER3);
    }

    @AfterEach
    void tearDown() {
        dbUtil.deleteCourses();
        dbUtil.deleteUsers();
    }
}
