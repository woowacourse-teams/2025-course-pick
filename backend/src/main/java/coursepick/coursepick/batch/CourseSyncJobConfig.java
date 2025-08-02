package coursepick.coursepick.batch;

import coursepick.coursepick.domain.Course;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

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
            ItemReader<Course> gpxFileReader,
            ItemProcessor<Course, Course> gpxToCourseProcessor,
            ItemWriter<Course> courseItemWriter
    ) {
        return new StepBuilder("syncStep", jobRepository)
                .<Course, Course>chunk(5, transactionManager)
                .reader(gpxFileReader)
                .processor(gpxToCourseProcessor)
                .writer(courseItemWriter)
                .build();
    }

    @Bean
    public JpaItemWriter<Course> courseItemWriter(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<Course> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}
