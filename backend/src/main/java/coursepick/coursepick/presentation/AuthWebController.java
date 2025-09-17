package coursepick.coursepick.presentation;

import coursepick.coursepick.application.AuthApplicationService;
import coursepick.coursepick.presentation.dto.LoginWebRequest;
import coursepick.coursepick.presentation.dto.LoginWebResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthWebController {

    private final AuthApplicationService authApplicationService;

    public AuthWebController(
            AuthApplicationService authApplicationService
    ) {
        this.authApplicationService = authApplicationService;
    }

    @PostMapping("admin/auth/login")
    public ResponseEntity<LoginWebResponse> login(@RequestBody LoginWebRequest request) {
        String token = authApplicationService.validateAndCreateToken(request.account(), request.password());
        return ResponseEntity.ok(new LoginWebResponse(token));
    }
}
