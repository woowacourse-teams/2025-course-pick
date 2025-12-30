package coursepick.coursepick.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// 인가 처리된 회원의 ID를 받기 위한 애노테이션입니다.
/// 인가 처리를 수행하지 않습니다. 따라서 `@Login` 없이 사용하면 예외가 발생합니다.
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserId {
}
