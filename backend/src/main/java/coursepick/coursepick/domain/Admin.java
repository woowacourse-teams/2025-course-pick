package coursepick.coursepick.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
@Getter
@Accessors(fluent = true)
public class Admin {

    @Id
    private final String id;

    private final String username;

    private final String password;

    public Admin(String username, String password) {
        this.id = null;
        this.username = username;
        this.password = password;
    }

    public boolean checkPassword(String password, PasswordHasher passwordHasher) {
        return passwordHasher.matches(password, this.password);
    }
}
