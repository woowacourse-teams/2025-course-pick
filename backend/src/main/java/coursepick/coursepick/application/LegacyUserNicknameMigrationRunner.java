package coursepick.coursepick.application;

import coursepick.coursepick.domain.user.NicknameGenerator;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LegacyUserNicknameMigrationRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final NicknameGenerator nicknameGenerator;

    @Override
    public void run(ApplicationArguments args) {
        List<User> legacyUsers = userRepository.findAllByNicknameIsNull();
        for (User user : legacyUsers) {
            user.assignNickname(nicknameGenerator);
            userRepository.save(user);
        }
        int updated = legacyUsers.size();
        if (updated > 0) {
            log.info("[MIGRATION] 닉네임이 없는 기존 사용자 {}명에게 닉네임을 부여했습니다.", updated);
        }
    }
}
