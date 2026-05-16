package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.user.Nickname;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserProvider;

public abstract class UserFixture {
    public static final User ADMIN_USER = new User(null, "adminId", "admin");
    public static final User TEST_USER = new User("userId1", UserProvider.KAKAO, "proviederId", new Nickname("testUser"));
    public static final User TEST_USER2 = new User("userId2", UserProvider.KAKAO, "proviederId", new Nickname("testUser2"));
    public static final User TEST_USER3 = new User("userId3", UserProvider.KAKAO, "proviederId", new Nickname("testUser3"));
}
