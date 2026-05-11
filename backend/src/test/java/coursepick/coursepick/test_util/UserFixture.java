package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserProvider;

public abstract class UserFixture {
    public static final User ADMIN_USER = new User(UserProvider.NONE, "adminId", "admin");
}
