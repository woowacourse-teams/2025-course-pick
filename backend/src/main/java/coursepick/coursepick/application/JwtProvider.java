package coursepick.coursepick.application;

import coursepick.coursepick.domain.Admin;

public interface JwtProvider {

    String createToken(Admin admin);

    void validateToken(String token);
}
