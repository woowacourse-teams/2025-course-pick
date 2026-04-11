package coursepick.coursepick.application;

import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.test_util.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AdminIdProviderTest extends AbstractIntegrationTest {

    @Autowired
    AdminIdProvider sut;

    @MockitoSpyBean
    MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sut, "adminId", null);
        mongoTemplate.save(new User(null, "admin"), "user");
    }

    @Test
    void 어드민_유저가_존재하면_어드민_ID를_정상적으로_반환한다() {
        var adminId = sut.get();

        assertThat(adminId).isEqualTo("admin");
    }

    @Test
    void 처음_조회_시_DB에서_가져오고_이후에는_캐싱된_값을_반환한다() {
        sut.get();
        sut.get();

        verify(mongoTemplate, times(1)).findOne(any(Query.class), eq(User.class));
    }
}
