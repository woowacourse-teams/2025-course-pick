package coursepick.coursepick.application;

import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.domain.Admin;
import coursepick.coursepick.domain.AdminRepository;
import coursepick.coursepick.domain.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final JwtProvider jwtProvider;
    private final PasswordHasher passwordHasher;
    private final AdminRepository adminRepository;

    public String validateAndCreateToken(String username, String password) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(ErrorType.LOGIN_FAIL::create);
        if (!admin.checkPassword(password, passwordHasher)) {
            throw ErrorType.LOGIN_FAIL.create();
        }
        return jwtProvider.createToken(admin);
    }
}
