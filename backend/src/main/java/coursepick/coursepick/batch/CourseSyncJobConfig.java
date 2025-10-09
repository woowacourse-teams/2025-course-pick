package coursepick.coursepick.batch;

import coursepick.coursepick.domain.Course;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.UncheckedIOException;

@Configuration
public class CourseSyncJobConfig {

    @Bean
    public Job courseSyncJob(JobRepository jobRepository, Step syncStep) {
        return new JobBuilder("courseSyncJob", jobRepository)
                .start(syncStep)
                .build();
    }

    @Bean
    public Step syncStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Course> courseReader,
            ItemProcessor<Course, Course> courseProcessor,
            ItemWriter<Course> courseWriter
    ) {
        return new StepBuilder("syncStep", jobRepository)
                .<Course, Course>chunk(5, transactionManager)
                .reader(courseReader)
                .processor(courseProcessor)
                .writer(courseWriter)
                .faultTolerant()
                .retryLimit(3)
                .retry(OptimisticLockingFailureException.class)
                .retry(PessimisticLockingFailureException.class)
                .retry(IOException.class)
                .retry(UncheckedIOException.class)
                .skipLimit(15)
                .skip(XMLStreamException.class)
                .skip(NumberFormatException.class)
                .skip(IllegalArgumentException.class)
                .skip(DataIntegrityViolationException.class)
                .build();
    }
}
