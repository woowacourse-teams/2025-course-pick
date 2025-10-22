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

        log.info("CourseSyncJob 종료");
        log.info("상태: {}", status);
        log.info("처리 시간: {}시간 {}분 {}초", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
        jobExecution.getStepExecutions().forEach(stepExecution ->
                log.info("Step [{}]: Read={}, Write={}, Skip={}",
                stepExecution.getStepName(),
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount()
        ));
    }
}
