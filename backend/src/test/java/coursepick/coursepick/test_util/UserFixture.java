package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.user.Nickname;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserProvider;
import org.bson.types.ObjectId;

import java.util.UUID;

public abstract class UserFixture {
    public static final User ADMIN_USER = User.testBuilder().build();

    public static User.UserBuilder user() {
        return User.testBuilder()
                .id(new ObjectId().toString())
                .provider(UserProvider.KAKAO)
                .providerId(UUID.randomUUID().toString())
                .nickname(Nickname.random());
    }
}
