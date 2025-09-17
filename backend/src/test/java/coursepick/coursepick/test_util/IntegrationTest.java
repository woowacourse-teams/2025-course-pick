package coursepick.coursepick.test_util;

import coursepick.coursepick.batch.CourseFileFetcher;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Import({GpxTestUtil.class, DatabaseTestUtil.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class IntegrationTest {

    @Autowired
    protected DatabaseTestUtil dbUtil;

    @MockitoBean
    protected CourseFileFetcher courseFileFetcher;

    @AfterEach
    void tearDown() {
        dbUtil.deleteCourses();
        dbUtil.deleteAdmins();
    }
}
