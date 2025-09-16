package coursepick.coursepick.presentation;

import coursepick.coursepick.application.AuthApplicationService;
import coursepick.coursepick.presentation.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthWebController {

    private final Long cookieMaxAge;
    private final AuthApplicationService authApplicationService;

    public AuthWebController(
            @Value("${auth.cookie.max-age}") Long cookieMaxAge,
            AuthApplicationService authApplicationService
    ) {
        this.cookieMaxAge = cookieMaxAge;
        this.authApplicationService = authApplicationService;
    }

    @PostMapping("admin/auth/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request) {
        String token = authApplicationService.validateAndCreateToken(request.account(), request.password());
        ResponseCookie cookie = ResponseCookie.from("token")
                .value(token)
                .httpOnly(true)
                .maxAge(cookieMaxAge)
                .path("/")
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }
}
