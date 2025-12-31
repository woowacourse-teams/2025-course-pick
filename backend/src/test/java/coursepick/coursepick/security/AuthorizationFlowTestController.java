package coursepick.coursepick.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizationFlowTestController {

    @Login
    @GetMapping("/test1")
    public void test1() {
    }

    @Login
    @GetMapping("/test2")
    public String test2(@UserId String id) {
        return id;
    }
}
