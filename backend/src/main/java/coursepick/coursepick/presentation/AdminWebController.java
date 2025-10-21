package coursepick.coursepick.presentation;

import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.presentation.dto.AdminLoginWebRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@Profile("dev")
public class AdminWebController {

    private static final String TOKEN_COOKIE_KEY = "admin-token";
    private final String adminPassword;

    public AdminWebController(@Value("${admin.token}") String adminPassword) {
        this.adminPassword = adminPassword;
    }

    @PostMapping("/admin/login")
    public ResponseEntity<Void> login(@RequestBody @Valid AdminLoginWebRequest request) {
        if (!adminPassword.equals(request.password())) {
            throw ErrorType.INVALID_ADMIN_PASSWORD.create();
        }
        ResponseCookie tokenCookie = ResponseCookie.from(TOKEN_COOKIE_KEY, adminPassword)
                .httpOnly(true)
                .maxAge(Duration.ofHours(1))
                .sameSite(Cookie.SameSite.STRICT.attributeValue())
                .path("/admin")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                .build();
    }
}
