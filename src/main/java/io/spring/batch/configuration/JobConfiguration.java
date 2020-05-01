/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.spring.batch.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.spring.batch.domain.Alerts;
import io.spring.batch.domain.ColumnRangePartitioner;
import io.spring.batch.domain.CustomerRowMapper;
import io.spring.batch.domain.ScheduledAlerts;

/**
 * @author Michael Minella
 */
@Configuration
public class JobConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource primaryDataSource;

    @Autowired
    @Qualifier("medDataSource")
    public DataSource medDataSource;

    private static final int GRID_SIZE = 4;
    
    private int appender = 7;

    @Bean
    public ColumnRangePartitioner partitioner() {

        ColumnRangePartitioner columnRangePartitioner = new ColumnRangePartitioner();

        columnRangePartitioner.setColumn("id");
        columnRangePartitioner.setDataSource(this.medDataSource);
        columnRangePartitioner.setTable("scheduledalerts");

        return columnRangePartitioner;
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<ScheduledAlerts> pagingItemReader(@Value("#{stepExecutionContext['minValue']}") Long minValue, @Value("#{stepExecutionContext['maxValue']}") Long maxValue) {

        System.out.println("reading " + minValue + " to " + maxValue + " Thread: " + Thread.currentThread().getName());

        JdbcPagingItemReader<ScheduledAlerts> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(this.medDataSource);
        reader.setFetchSize(1000);
        reader.setRowMapper(new CustomerRowMapper());

        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("id, title, description");
        queryProvider.setFromClause("from scheduledalerts");
        queryProvider.setWhereClause(
                "where time <= CURRENT_TIMESTAMP and isscheduled=false and isactive = true and " + "id >= " + minValue + " and id <= " + maxValue);

        Map<String, Order> sortKeys = new HashMap<>(1);

        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);

        return reader;
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Alerts> customerItemWriter() {

        System.out.println("writer Thread: " + Thread.currentThread().getName());
        JdbcBatchItemWriter<Alerts> itemWriter = new JdbcBatchItemWriter<>();
        
        itemWriter.setDataSource(this.medDataSource);
        itemWriter.setSql("INSERT INTO public.alerts(" + 
                "     type, title, description, comment, \"time\", status, patientid, caremanagerid, createdby, creationdate, updatedby, updationdate, isactive, alertfor, repeat, category, elementids, scheduledtime, isfollowupquestion, followupsince, isspecial, subtype, scheduledalertsid, parentid, multiplepatientid, priority)" + 
                "    VALUES ( 1, :title, :description, 'comment', current_timestamp, 60, null, null, null, null, null, null, true, null, null, null, null, null, false, null, null, null, null, :id, null, null)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    @Bean
    public TaskExecutor poolTaskExecutor() {

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(4);
        // taskExecutor.setCorePoolSize(2);
        taskExecutor.setKeepAliveSeconds(5);
        taskExecutor.setAllowCoreThreadTimeOut(true);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @Bean
    public TaskExecutor taskExecutor() {

        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public Step step1() throws Exception {

        return stepBuilderFactory.get("step1" + appender).partitioner(slaveStep().getName(), partitioner()).step(slaveStep()).gridSize(GRID_SIZE)
                .taskExecutor(taskExecutor()).listener(chunkListener()).build();
    }

    @Bean
    public MyChunkListener chunkListener(){
        return new MyChunkListener();
    }

    @Bean
    public Step slaveStep() {

        return stepBuilderFactory.get("slaveStep" + appender).<ScheduledAlerts, Alerts>chunk(1000).reader(pagingItemReader(null, null))
                .writer(customerItemWriter()).build();
    }

    @Bean
    public Job job() throws Exception {

        System.out.println("primaryDataSource-" + ReflectionToStringBuilder.toString(this.primaryDataSource));
        System.out.println("medDataSource-" + ReflectionToStringBuilder.toString(medDataSource));
        return jobBuilderFactory.get("job" + appender).start(step1()).build();
    }
}
