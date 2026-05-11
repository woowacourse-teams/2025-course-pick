package coursepick.coursepick.domain.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndex(name = "idx_provider_providerId", def = "{'provider': 1, 'providerId': 1}", unique = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC, onConstructor_ = @PersistenceCreator)
@Builder(builderMethodName = "testBuilder")
@Getter
@Accessors(fluent = true)
public class User {

    @Id
    private final String id;
    private final UserProvider provider;
    private final String providerId;
    private Nickname nickname;

    public static User create(
            UserProvider provider,
            String providerId
    ) {
        return new User(
                null,
                provider,
                providerId,
                Nickname.random()
        );
    }

    public void assignRandomNickname() {
        this.nickname = Nickname.random();
    }
}
