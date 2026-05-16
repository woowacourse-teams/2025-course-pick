plugins {
    java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.epages.restdocs-api-spec") version "0.19.4"
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
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.apache.commons:commons-lang3")

    // Zstandard Compression
    implementation("com.github.luben:zstd-jni:1.5.7-1")

    // JSpecify
    compileOnly("org.jspecify:jspecify:1.0.0")

    // DB Connector
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // JJWT
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

    // Spatial4J
    implementation("org.locationtech.spatial4j:spatial4j:0.8")

    // Logback Logstash Encoder (JSON Format)
    implementation("net.logstash.logback:logstash-logback-encoder:8.1")

    // JVM/Spring Metric To CloudWatch
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Prometheus Metrics
    implementation("io.micrometer:micrometer-registry-prometheus")

    // MockWebServer
    testImplementation("com.squareup.okhttp3:mockwebserver3:5.1.0")

    // Spring REST Docs (MockMvc)
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

    // restdocs-api-spec (OpenAPI 3 스펙 생성)
    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.19.4")

    // Test
    testImplementation("com.tngtech.archunit:archunit:1.1.0")
    testRuntimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring30x:4.21.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}


val isProdProfile = project.findProperty("profile") == "prod"

if (!isProdProfile) {
    // openapi3 태스크 실행 후, JWT Bearer 인증 정보와 파라미터 예시를 OpenAPI 스펙에 주입
    openapi3 {
        setServer("http://localhost:8080")
        title = "코스픽 API"
        version = "v1"
        format = "json"
        outputDirectory = layout.buildDirectory.dir("api-spec").get().asFile.path
    }

    tasks.register("injectOpenApiSecurity") {
        dependsOn("openapi3")
        doLast {
            val specFile = layout.buildDirectory.file("api-spec/openapi3.json").get().asFile
            coursepick.OpenApiProcessor.process(specFile)
        }
    }

    // openapi3 태스크 + 보안 주입 후, 생성된 스펙 파일을 static 리소스로 복사
    tasks.register<Copy>("copyOpenApiSpec") {
        dependsOn("injectOpenApiSecurity")
        from(layout.buildDirectory.dir("api-spec"))
        into("src/main/resources/static/docs")
    }

    tasks.build {
        dependsOn("copyOpenApiSpec")
    }

    tasks.bootJar {
        dependsOn("copyOpenApiSpec")
    }

    tasks.clean {
        delete("src/main/resources/static/docs")
    }
}
