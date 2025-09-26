FROM amazoncorretto:21

WORKDIR /app

ENV JAVA_OPTS="-Xms512m -Xmx512m -XX:+UseParallelGC -XX:MaxGCPauseMillis=100"
COPY build/libs/*.jar coursepick-backend.jar

ENTRYPOINT ["java", "-jar", "coursepick-backend.jar"]
