package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.user.Nickname;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserProvider;

public abstract class UserFixture {
    public static final User ADMIN_USER = new User("673c242c332e2c244c000001", UserProvider.NONE, "adminId", new Nickname("admin"));
    public static final User TEST_USER = new User("673c242c332e2c244c000002", UserProvider.KAKAO, "proviederId1", new Nickname("testUser"));
    public static final User TEST_USER2 = new User("673c242c332e2c244c000003", UserProvider.KAKAO, "proviederId2", new Nickname("testUser2"));
    public static final User TEST_USER3 = new User("673c242c332e2c244c000004", UserProvider.KAKAO, "proviederId3", new Nickname("testUser3"));
}
