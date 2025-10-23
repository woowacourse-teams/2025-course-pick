package coursepick.coursepick.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class CourseSyncJobListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
        BatchStatus status = jobExecution.getStatus();
        Duration duration = Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime());

        StringBuilder stepInfo = new StringBuilder();
        jobExecution.getStepExecutions().forEach(stepExecution ->
                stepInfo.append(String.format(" | Step [%s]: Read=%d, Write=%d, Skip=%d",
                        stepExecution.getStepName(),
                        stepExecution.getReadCount(),
                        stepExecution.getWriteCount(),
                        stepExecution.getSkipCount()
                ))
        );

        log.info("[CourseSyncJob 종료] 상태: {} | 처리 시간: {}시간 {}분 {}초{}",
                status,
                duration.toHoursPart(),
                duration.toMinutesPart(),
                duration.toSecondsPart(),
                stepInfo);
    }
}
