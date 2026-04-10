FROM amazoncorretto:21

WORKDIR /app

COPY build/libs/*.jar coursepick-backend.jar

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/v2.26.1/download/opentelemetry-javaagent.jar opentelemetry-javaagent.jar

ENTRYPOINT ["java", "-javaagent:opentelemetry-javaagent.jar", "-jar", "coursepick-backend.jar"]
