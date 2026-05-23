package coursepick.coursepick;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.Architectures.LayeredArchitecture;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 프로젝트 전체의 아키텍처 및 코딩 컨벤션을 검증하는 메타 테스트 클래스입니다.
 * 'test_util' 패키지는 검증 대상에서 제외됩니다.
 */
@Order(1)
class ArchTest {

    // 분석할 대상 패키지를 지정합니다.
    private final JavaClasses allFiles = new ClassFileImporter().importPackages("coursepick.coursepick");

    // test_util 패키지를 제외한 클래스들만 필터링합니다.
    private final JavaClasses importedClasses = allFiles.that(new DescribedPredicate<JavaClass>("exclude test_util package") {
        @Override
        public boolean test(JavaClass input) {
            return !input.getPackageName().contains("test_util");
        }
    });

    /**
     * 레이어드 아키텍처의 의존성 방향을 검증합니다.
     * - Presentation -> Application -> Domain 순으로 의존해야 합니다.
     * - 특히 도메인(Domain) 계층은 다른 어떤 계층도 의존하지 않는 순수한 상태를 유지해야 합니다.
     */
    @Disabled("추후 적용 예정")
    @Test
    void 아키텍처_의존성_규칙을_준수해야_한다() {
        LayeredArchitecture architecture =
                Architectures.layeredArchitecture()
                        .consideringAllDependencies()
                        .layer("Presentation").definedBy("..presentation..")
                        .layer("Application").definedBy("..application..")
                        .layer("Domain").definedBy("..domain..")
                        .layer("Infrastructure").definedBy("..infrastructure..")

                        .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
                        .whereLayer("Application").mayOnlyBeAccessedByLayers("Presentation", "Infrastructure")
                        .whereLayer("Infrastructure").mayOnlyBeAccessedByLayers("Presentation", "Application")
                        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Presentation");

        architecture.check(importedClasses);
    }

    /**
     * 테스트 코드 작성 시 @DisplayName 어노테이션을 사용하는 대신,
     * 메서드 이름 자체를 한글로 작성하여 테스트 의도를 명확히 전달하도록 강제합니다.
     */
    @Test
    void 모든_테스트_메서드는_DisplayName_어노테이션_없이_한글로_작성되어야_한다() {
        ArchRule rule = methods()
                .that().areAnnotatedWith(Test.class)
                .or().areAnnotatedWith(ParameterizedTest.class)
                .should().notBeAnnotatedWith(DisplayName.class)
                .andShould(new ArchCondition<JavaMethod>("메서드 이름에 한글이 포함되어야 함") {
                    @Override
                    public void check(JavaMethod item, ConditionEvents events) {
                        if (!item.getName().matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣].*")) {
                            String message = String.format("%s 메서드 이름에 한글이 포함되어 있지 않습니다.", item.getFullName());
                            events.add(SimpleConditionEvent.violated(item, message));
                        }
                    }
                });

        rule.check(importedClasses);
    }

    /**
     * 테스트 코드 내의 지역 변수 선언 시 명시적 타입을 사용하는 대신 var 키워드를 사용하도록 강제합니다.
     * 객체 생성(new)뿐만 아니라 메서드 리턴 값을 할당받는 모든 지점을 검증합니다.
     * 'test_util' 패키지는 검증에서 제외됩니다.
     */
    @Test
    void 테스트_코드의_지역_변수_선언시_var를_사용해야_한다() throws IOException {
        Path testSourcePath = Paths.get("src/test/java/coursepick/coursepick");

        // 지역 변수 선언 시 명시적 타입 사용을 탐지하는 패턴 (메서드 내부를 가정하여 8자 이상의 공백으로 시작)
        // 기본형 및 모든 참조형(제네릭 포함) 변수 선언 시 var를 사용하지 않는 경우 탐지 (여러 줄 초기화 고려하여 세미콜론 검사 제거)
        Pattern explicitTypePattern = Pattern.compile("^ {8,}(final\\s+)?([A-Z][a-zA-Z0-9<>._\\[\\], ]*|int|long|boolean|double|float|char|byte|short)\\s+([a-z][a-zA-Z0-9_]*)\\s*=");

        try (Stream<Path> paths = Files.walk(testSourcePath)) {
            List<String> violations = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !path.toString().contains("ArchTest.java"))
                    .filter(path -> !path.toString().contains("test_util")) // test_util 패키지 제외
                    .flatMap(path -> {
                        try {
                            List<String> lines = Files.readAllLines(path);
                            Stream.Builder<String> violationsPerFile = Stream.builder();
                            for (int i = 0; i < lines.size(); i++) {
                                String line = lines.get(i);
                                if (!line.contains("private ") && !line.contains("public ") && !line.contains("protected ")
                                        && explicitTypePattern.matcher(line).find()) {
                                    violationsPerFile.add(String.format("%s:%d - var를 사용해야 합니다: %s",
                                            path.getFileName(), i + 1, line.trim()));
                                }
                            }
                            return violationsPerFile.build();
                        } catch (IOException e) {
                            return Stream.empty();
                        }
                    })
                    .toList();

            assertThat(violations)
                    .withFailMessage("테스트 코드 내에서 'var'를 사용하지 않은 지점이 발견되었습니다:\n" + String.join("\n", violations))
                    .isEmpty();
        }
    }

    /**
     * 테스트 클래스와 그 내부의 모든 필드, 메서드는 private을 제외하고는
     * 모두 접근 제어자가 없는 package-private 상태여야 합니다.
     */
    @Test
    void 테스트_코드의_구성_요소는_private_메서드를_제외하고_package_private_이어야_한다() {
        ArchRule classRule = classes()
                .that().haveSimpleNameEndingWith("Test")
                .should().bePackagePrivate()
                .because("테스트 클래스는 package-private이어야 합니다.");

        ArchRule fieldRule = fields()
                .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Test")
                .and().areNotPrivate()
                .should().bePackagePrivate()
                .because("테스트 클래스의 필드는 package-private 또는 private이어야 합니다.");

        ArchRule methodRule = methods()
                .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Test")
                .and().areNotPrivate()
                .should().bePackagePrivate()
                .because("테스트 메서드와 관련 라이프사이클 메서드는 package-private 또는 private이어야 합니다.");

        classRule.check(importedClasses);
        fieldRule.check(importedClasses);
        methodRule.check(importedClasses);
    }
}
