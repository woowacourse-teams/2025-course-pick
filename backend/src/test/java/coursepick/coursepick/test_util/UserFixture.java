package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.user.User;

public abstract class UserFixture {
    public static final User ADMIN_USER = User.testBuilder().build();
}
