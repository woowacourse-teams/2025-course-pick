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

configurations.all {
    exclude(group = "commons-logging", module = "commons-logging")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
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
    implementation("com.google.api-client:google-api-client:2.8.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev20250723-2.0.0")

    // Google Maps
    implementation("com.google.maps:google-maps-services:2.2.0")

    // MockWebServer
    testImplementation("com.squareup.okhttp3:mockwebserver3:5.1.0")

    // Test
    testRuntimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring30x:4.21.0")
    testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    implementation("org.springframework.security:spring-security-crypto")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
