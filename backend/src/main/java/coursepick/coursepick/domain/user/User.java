package coursepick.coursepick.domain.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndex(name = "idx_provider_providerId", def = "{'provider': 1, 'providerId': 1}", unique = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC, onConstructor_ = @PersistenceCreator)
@Getter
@Accessors(fluent = true)
public class User {

    @Id
    private final String id;
    private final UserProvider provider;
    private final String providerId;

    public User(UserProvider provider, String providerId) {
        this.id = null;
        this.provider = provider;
        this.providerId = providerId;
    }
}
