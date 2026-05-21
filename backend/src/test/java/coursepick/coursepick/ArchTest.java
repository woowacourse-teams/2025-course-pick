package coursepick.coursepick;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.Architectures.LayeredArchitecture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

public class ArchTest {

    private final JavaClasses importedClasses = new ClassFileImporter().importPackages("coursepick.coursepick");

    @Test
    @DisplayName("test1: 클래스 이름에 'Type'이 포함되어서는 안 된다 (ErrorCode 등 지향)")
    void test1() {
        ArchRule rule = classes()
                .should().haveSimpleNameNotContaining("Type");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("test2: 필드 이름에 'type'이 포함되어서는 안 된다")
    void test2() {
        ArchRule rule = fields()
                .should().notHaveName("type");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("test3: 레이어드 아키텍처 의존성 규칙을 준수해야 한다")
    void test3() {
        // 1. 레이어 정의: 프로젝트의 각 패키지를 논리적인 레이어로 정의합니다.
        LayeredArchitecture architecture =
            Architectures.layeredArchitecture()
                .consideringAllDependencies()
                .layer("Presentation").definedBy("..presentation..")
                .layer("Application").definedBy("..application..")
                .layer("Domain").definedBy("..domain..")
                .layer("Infrastructure").definedBy("..infrastructure..")

                // 2. Presentation(Controller 등)은 최상위 계층으로, 다른 어떤 레이어에서도 접근할 수 없습니다.
                .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()

                // 3. Application(Service 등)은 Presentation과 Infrastructure에서만 접근 가능합니다.
                //    중요: 여기서 'Domain'을 제외함으로써 도메인이 애플리케이션 레이어를 의존하는 것을 방지합니다.
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Presentation", "Infrastructure")

                // 4. Infrastructure는 외부 시스템과의 연결을 담당하며, 도메인 레이어에서 접근해서는 안 됩니다.
                .whereLayer("Infrastructure").mayOnlyBeAccessedByLayers("Presentation", "Application")

                // 5. Domain은 시스템의 핵심 비즈니스 로직을 담고 있으며, 가장 안쪽 계층입니다.
                //    Domain은 모든 레이어(App, Infra, Presentation)에서 접근할 수 있지만,
                //    반대로 Domain 내부의 코드가 다른 레이어(Application, Infrastructure, Presentation)의 코드를
                //    참조하거나 의존하는 순간 위 2, 3, 4번 규칙에 의해 테스트가 실패하게 됩니다.
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Presentation");

        // 정의한 아키텍처 규칙이 준수되고 있는지 검증을 실행합니다.
        architecture.check(importedClasses);
    }
}
