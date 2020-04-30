package com.proj;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.proj.batch.listner.JobCompletionNotificationListener;
import com.proj.batch.processor.MemberItemProcessor;
import com.proj.batch.processor.MemberItemProcessor2;
import com.proj.domain.Member;

// tag::setup[]
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public JdbcCursorItemReader<Member> reader(DataSource dataSource) {

        return new JdbcCursorItemReaderBuilder<Member>().dataSource(dataSource).name("creditReader").sql("select firstname, lastname from member")
                .rowMapper(new BeanPropertyRowMapper<>(Member.class)).build();

    }

    @Bean
    public MemberItemProcessor processor() {

        return new MemberItemProcessor();
    }

    @Bean
    public MemberItemProcessor2 processor2() {

        return new MemberItemProcessor2();
    }

    @Bean
    public JdbcBatchItemWriter<Member> writer(DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<Member>().itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO member (firstname, lastname) VALUES (:firstName, :lastName)").dataSource(dataSource).build();
    }
// end::readerwriterprocessor[]

// tag::jobstep[]
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1, Step step2) {

        return this.jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer()).listener(listener).flow(step1).next(step2).end()
                .build();
    }

    @Bean
    public Step step1(JdbcCursorItemReader<Member> reader, JdbcBatchItemWriter<Member> writer) {

        return this.stepBuilderFactory.get("step1").<Member, Member>chunk(10).reader(reader).processor(processor()).writer(writer).build();
    }

    @Bean
    public Step step2(JdbcCursorItemReader<Member> reader, JdbcBatchItemWriter<Member> writer) {

        return this.stepBuilderFactory.get("step2").<Member, Member>chunk(10).reader(reader).processor(processor2()).writer(writer).build();
    }
// end::jobstep[]
}
