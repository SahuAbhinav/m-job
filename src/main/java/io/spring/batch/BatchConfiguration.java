package io.spring.batch;

import javax.sql.DataSource;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class BatchConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource")
    public DataSourceProperties fooDataSourceProperties() {

        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.configuration")
    public DataSource fooDataSource() {

        DataSource ds = fooDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
        return ds; 
    }

    @Bean
    @ConfigurationProperties("medsolis.datasource")
    public DataSourceProperties medDataSourceProperties() {

        DataSourceProperties props = new DataSourceProperties();
        return props;
    }

    @Bean(name = "medDataSource")
    @ConfigurationProperties("medsolis.datasource.configuration")
    public DataSource medDataSource() {

        DataSourceProperties dsp = medDataSourceProperties();
        DataSource ds = dsp.initializeDataSourceBuilder().build();
        return ds;
    }
}