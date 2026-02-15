package coursepick.coursepick.presentation;

import coursepick.coursepick.application.UserApplicationService;
import coursepick.coursepick.domain.user.Authentication;
import coursepick.coursepick.presentation.api.UserWebApi;
import coursepick.coursepick.presentation.dto.SignWebRequest;
import coursepick.coursepick.presentation.dto.SignWebResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserV1WebController implements UserWebApi {

    private final UserApplicationService userApplicationService;

    @Override
    @PostMapping("/login/kakao")
    public SignWebResponse sign(@RequestBody SignWebRequest request) {
        Authentication authentication = userApplicationService.registerOrLoginAndGetAuthentication(request.accessToken());
        return new SignWebResponse(authentication.accessToken());
    }
}
