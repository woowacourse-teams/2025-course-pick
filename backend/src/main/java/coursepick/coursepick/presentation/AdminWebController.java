package coursepick.coursepick.presentation;

import coursepick.coursepick.presentation.dto.AdminLoginWebRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("dev")
public class AdminWebController {

    public static final String TOKEN_COOKIE_KEY = "admin-token";
    @Value("${admin.token}")
    private String adminPassword;

    @PostMapping("/admin/login")
    public ResponseEntity<Void> login(@RequestBody AdminLoginWebRequest request) {
        if (!adminPassword.equals(request.password())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ResponseCookie tokenCookie = ResponseCookie.from(TOKEN_COOKIE_KEY, adminPassword)
                .httpOnly(true)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                .build();
    }
}
