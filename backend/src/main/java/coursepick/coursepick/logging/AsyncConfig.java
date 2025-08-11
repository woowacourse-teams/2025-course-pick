package coursepick.coursepick.logging;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    /**
     * MDC를 전파하는 TaskDecorator가 등록된 Executor를 반환합니다.
     * <br>
     * MDC를 전파받아야 하는 비동기 작업에서 주입받아 사용하면 됩니다.
     * <br>
     * 자세한 Executor 설정은 <a href="https://docs.spring.io/spring-framework/reference/integration/scheduling.html">스프링 공식문서</a>를 참고했습니다.
     */
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.initialize();
        return executor;
    }

    /**
     * 작업 스레드의 MDC를 백업한 후에,
     * 현재 스레드의 MDC를 작업 스레드에 전파하고,
     * 작업 스레드의 작업을 실행합니다.
     * 작업이 끝난 후에는 작업 스레드의 MDC를 기존으로 복구시킵니다.
     * <br>
     * 위 로직을 통해서 작업 스레드가 스레드풀에 반환되더라도 문제가 발생하지 않습니다.
     */
    private static class MdcTaskDecorator implements TaskDecorator {

        @Override
        public Runnable decorate(Runnable runnable) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();

            return () -> {
                Map<String, String> previous = MDC.getCopyOfContextMap();
                try {
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    } else {
                        MDC.clear();
                    }
                    runnable.run();
                } finally {
                    if (previous != null) {
                        MDC.setContextMap(previous);
                    } else {
                        MDC.clear();
                    }
                }
            };
        }
    }
}
