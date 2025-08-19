FROM amazoncorretto:21

WORKDIR /app

COPY build/libs/*.jar coursepick-backend.jar

ENTRYPOINT ["java", "-jar", "coursepick-backend.jar"]
