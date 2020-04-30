package io.spring.batch;

import javax.sql.DataSource;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

//@Configuration
public class BatchConfiguration2 {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties fooDataSourceProperties() {

        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.configuration")
    public DataSource fooDataSource() {

        DataSource ds = fooDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
        System.out.println("primary-"+ReflectionToStringBuilder.toString(ds));
        return ds; 
    }

    @Bean
    @ConfigurationProperties("medsolis.datasource")
    public DataSourceProperties barDataSourceProperties() {

        DataSourceProperties props = new DataSourceProperties();
        return props;
    }

    @Bean(name = "medDataSource")
    @ConfigurationProperties("medsolis.datasource")
    public DataSource barDataSource() {

        DataSourceProperties dsp = barDataSourceProperties();
        //System.out.println(ReflectionToStringBuilder.toString(dsp));
        DataSource ds = dsp.initializeDataSourceBuilder().build();
        System.out.println(ReflectionToStringBuilder.toString(ds));
        return ds;
    }
}