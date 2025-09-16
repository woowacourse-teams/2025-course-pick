package coursepick.coursepick.domain;

import coursepick.coursepick.application.PasswordEncoder;
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

    private final String account;

    private final String password;

    public boolean checkPassword(String password, PasswordEncoder passwordEncoder) {
        System.out.println(passwordEncoder.hash(password));
        System.out.println("real: " + this.password);
        return passwordEncoder.matches(password, this.password);
    }
}
