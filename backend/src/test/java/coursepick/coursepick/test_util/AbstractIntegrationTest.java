package coursepick.coursepick.test_util;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import({GpxTestUtil.class, DatabaseTestUtil.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class AbstractIntegrationTest extends AbstractMockServerTest {

    @Autowired
    protected DatabaseTestUtil dbUtil;

    @AfterEach
    void tearDown() {
        dbUtil.deleteCourses();
    }
}
