FROM amazoncorretto:21-alpine

ARG PROFILE
ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ARG ADMIN_TOKEN

COPY build/libs/*.jar app.jar

ARG PROFILE=${PROFILE}
ARG DB_URL=${DB_URL}
ARG DB_USERNAME=${DB_USERNAME}
ARG DB_PASSWORD=${DB_PASSWORD}
ARG ADMIN_TOKEN=${ADMIN_TOKEN}

EXPOSE 8080

CMD ["java", "-jar", "-Dspring.profiles.active=${PROFILE}", "app.jar"]
