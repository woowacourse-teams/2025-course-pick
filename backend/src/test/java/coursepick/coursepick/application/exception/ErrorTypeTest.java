package coursepick.coursepick.application.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import coursepick.coursepick.presentation.dto.ErrorResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

class ErrorTypeTest {

    @Test
    void ErrorType으로_예외_생성_시_메시지에_에러_코드가_포함된다() {
        var errorType = ErrorType.INVALID_LATITUDE_RANGE;
        var argument = "100";

        var exception = errorType.create(argument);

        assertThat(exception.getMessage()).contains("[ErrorCode = INVALID_LATITUDE_RANGE]");
        assertThat(exception.getMessage()).contains("위도는 -90 이상, 90 이하이어야 합니다. 입력값=100");
    }

    @Test
    void ErrorResponse는_예외_메시지에서_에러_코드를_정상적으로_추출한다() {
        var errorType = ErrorType.NOT_EXIST_COURSE;
        var exception = errorType.create("1");

        var response = ErrorResponse.from(exception);

        assertThat(response.errorCode()).isEqualTo("NOT_EXIST_COURSE");
    }

    @Test
    void BindingResult로부터_ErrorResponse_생성_시_에러_코드는_INVALID_INPUT이다() {
        var bindingResult = mock(BindingResult.class);
        given(bindingResult.getFieldErrors()).willReturn(List.of(
                new FieldError("object", "field", "defaultMessage")
        ));

        var response = ErrorResponse.from(bindingResult);

        assertThat(response.errorCode()).isEqualTo("INVALID_INPUT");
        assertThat(response.message()).isEqualTo("defaultMessage");
    }

    @Test
    void 에러_코드가_없는_예외_메시지로부터_ErrorResponse_생성_시_에러_코드는_UNKNOWN_ERROR이다() {
        var exception = new RuntimeException("일반 예외 메시지");

        var response = ErrorResponse.from(exception);

        assertThat(response.errorCode()).isEqualTo("UNKNOWN_ERROR");
    }
}
