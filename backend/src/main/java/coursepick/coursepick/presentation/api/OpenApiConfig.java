package coursepick.coursepick.presentation.api;

import coursepick.coursepick.application.exception.ErrorType;
import coursepick.coursepick.application.exception.QueryTimeoutException;
import coursepick.coursepick.application.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Profile("!prod")
@Slf4j
@OpenAPIDefinition(info = @Info(title = "코스픽 API"))
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "JWT 토큰을 입력하세요. 'Bearer ' 접두사는 자동으로 추가됩니다.")
@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/v1/**")
                .addOperationCustomizer(customizeOperation())
                .build();
    }

    public OperationCustomizer customizeOperation() {
        return (operation, handlerMethod) -> {
            ApiErrorExceptionsExample apiErrorExceptionsExample = AnnotatedElementUtils.findMergedAnnotation(
                    handlerMethod.getMethod(), ApiErrorExceptionsExample.class);
            if (apiErrorExceptionsExample == null) {
                try {
                    Method interfaceMethod = CourseWebApi.class.getMethod(
                            handlerMethod.getMethod().getName(), handlerMethod.getMethod().getParameterTypes());
                    apiErrorExceptionsExample = AnnotatedElementUtils.findMergedAnnotation(interfaceMethod,
                            ApiErrorExceptionsExample.class);
                } catch (Exception e) {
                }
            }
            if (apiErrorExceptionsExample != null) {
                generateErrorResponseExample(operation, apiErrorExceptionsExample.value());
            }

            if ((operation.getParameters() != null && !operation.getParameters().isEmpty())
                    || operation.getRequestBody() != null) {
                addDefaultValidationError(operation);
            }

            return operation;
        };
    }

    private void addDefaultValidationError(io.swagger.v3.oas.models.Operation operation) {
        ApiResponses responses = operation.getResponses();
        ApiResponse apiResponse = responses.getOrDefault("400", new ApiResponse());

        String defaultMessage = "- 잘못된 파라미터 (예: 필수 파라미터 누락, null 값, 형식 오류 등)\n";
        String existingDescription = apiResponse.getDescription();

        if (existingDescription == null) {
            existingDescription = "발생 가능한 400 에러:\n";
        }

        if (!existingDescription.contains("잘못된 파라미터")) {
            apiResponse.setDescription(existingDescription + defaultMessage);
            responses.addApiResponse("400", apiResponse);
        }
    }

    private String getStatusCode(ErrorType errorType) {
        Class<? extends RuntimeException> exceptionClass = errorType.getExceptionClass();
        if (IllegalArgumentException.class.isAssignableFrom(exceptionClass))
            return "400";
        if (NoSuchElementException.class.isAssignableFrom(exceptionClass))
            return "404";
        if (SecurityException.class.isAssignableFrom(exceptionClass)
                || UnauthorizedException.class.isAssignableFrom(exceptionClass))
            return "401";
        if (IllegalStateException.class.isAssignableFrom(exceptionClass))
            return "409";
        if (QueryTimeoutException.class.isAssignableFrom(exceptionClass))
            return "503";
        return "500";
    }

    private void generateErrorResponseExample(io.swagger.v3.oas.models.Operation operation, ErrorType[] errorTypes) {
        ApiResponses responses = operation.getResponses();

        Map<String, List<ErrorType>> statusToErrors = Arrays.stream(errorTypes)
                .collect(Collectors.groupingBy(this::getStatusCode));

        for (Map.Entry<String, List<ErrorType>> entry : statusToErrors.entrySet()) {
            String statusCode = entry.getKey();
            List<ErrorType> errors = entry.getValue();

            ApiResponse apiResponse = responses.getOrDefault(statusCode, new ApiResponse());

            StringBuilder description = new StringBuilder("발생 가능한 " + statusCode + " 에러:\n");
            for (ErrorType errorType : errors) {
                description.append("- ").append(errorType.getMessageForApiDoc()).append("\n");
            }

            apiResponse.setDescription(description.toString());
            responses.addApiResponse(statusCode, apiResponse);
        }
    }
}
