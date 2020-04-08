package com.proj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobsBatchApplication {

    public static void main(String[] args) {

        SpringApplication.run(JobsBatchApplication.class, args);
    }

}
