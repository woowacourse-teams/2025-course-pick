package coursepick.coursepick.application;

import coursepick.coursepick.logging.LogContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info("CourseSyncJob 자동 시작");
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("run.id", "scheduled-" + System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(courseSyncJob, jobParameters);
        } catch (Exception e) {
            log.warn("[EXCEPTION] CourseSyncJob 자동 실행 중 예외 발생", LogContent.exception(e));
        }
    }

    @Async
    public void runCourseSyncJob() {
        log.info("CourseSyncJob 수동 시작");
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("run.id", "manual-" + System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(courseSyncJob, jobParameters);
        } catch (Exception e) {
            log.warn("[EXCEPTION] CourseSyncJob 수동 실행 중 예외 발생", LogContent.exception(e));
        }
    }
}
