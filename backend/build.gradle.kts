plugins {
    java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "coursepick"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")
    implementation("org.springframework.boot:spring-boot-starter-batch")

    // DB Connector
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Spatial4J
    implementation("org.locationtech.spatial4j:spatial4j:0.8")

    // GPX Parser
    implementation("io.jenetics:jpx:3.2.1")

    // Logback Logstash Encoder (JSON Format)
    implementation("net.logstash.logback:logstash-logback-encoder:8.1")

    // JVM/Spring Metric To CloudWatch
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-metrics:3.4.0")

    // Google Drive
    implementation("com.google.api-client:google-api-client:2.7.2")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.37.1")
    implementation("com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0")

    // Test
    testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
