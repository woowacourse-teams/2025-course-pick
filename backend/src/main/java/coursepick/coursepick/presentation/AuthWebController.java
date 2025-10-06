package coursepick.coursepick.presentation;

import coursepick.coursepick.application.AuthApplicationService;
import coursepick.coursepick.presentation.dto.LoginWebRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthWebController {

    private final AuthApplicationService authApplicationService;

    @PostMapping("/admin/auth/login")
    public ResponseEntity<Void> login(HttpServletRequest request, @RequestBody LoginWebRequest requestBody) {
        authApplicationService.validateAndCreateToken(requestBody.username(), requestBody.password());

        HttpSession session = request.getSession();
        session.setAttribute("expiredTime", System.currentTimeMillis() + 30 * 60 * 1000);

        return ResponseEntity.ok().build();
    }
}
