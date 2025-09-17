package coursepick.coursepick.presentation;

import coursepick.coursepick.application.AuthApplicationService;
import coursepick.coursepick.presentation.api.AuthWebApi;
import coursepick.coursepick.presentation.dto.LoginWebRequest;
import coursepick.coursepick.presentation.dto.LoginWebResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthWebController implements AuthWebApi {

    private final AuthApplicationService authApplicationService;


    @PostMapping("admin/auth/login")
    public ResponseEntity<LoginWebResponse> login(@RequestBody LoginWebRequest request) {
        String token = authApplicationService.validateAndCreateToken(request.account(), request.password());
        return ResponseEntity.ok(new LoginWebResponse(token));
    }
}
