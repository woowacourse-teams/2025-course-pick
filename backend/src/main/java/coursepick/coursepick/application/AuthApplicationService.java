package coursepick.coursepick.application;

import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.domain.Admin;
import coursepick.coursepick.domain.AdminRepository;
import coursepick.coursepick.presentation.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;

    public String login(LoginRequest request) {
        Admin admin = adminRepository.findByAccount(request.account())
                .orElseThrow(ErrorType.LOGIN_FAIL::create);
        if (!admin.checkPassword(request.password(), passwordEncoder)) {
            throw ErrorType.LOGIN_FAIL.create();
        }
        return jwtProvider.createToken(admin);
    }
}
