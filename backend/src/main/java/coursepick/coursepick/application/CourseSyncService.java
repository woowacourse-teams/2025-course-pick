package coursepick.coursepick.application;

import coursepick.coursepick.logging.LogContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseSyncService {

    private final JobLauncher jobLauncher;
    private final Job courseSyncJob;

    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")
    public void runScheduledCourseSyncJob() {
        executeJob("scheduled-", "자동");
    }

    @Async("asyncExecutor")
    public void runCourseSyncJob() {
        executeJob("manual-", "수동");
    }

    private void executeJob(String prefix, String jobType) {
        String runId = prefix + System.currentTimeMillis();
        MDC.put("run.id", runId);
        try {
            log.info("CourseSyncJob {} 시작", jobType);
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("run.id", runId)
                    .toJobParameters();
            jobLauncher.run(courseSyncJob, jobParameters);
        } catch (Exception e) {
            log.warn("[EXCEPTION] CourseSyncJob {} 실행 중 예외 발생 {}", jobType, LogContent.exception(e));
        } finally {
            MDC.remove("run.id");
        }
    }
}
