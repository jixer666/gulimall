package com.ljx.ssoclient1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class GulimallOssClient1Application {
    public static void main(String[] args) {
        SpringApplication.run(GulimallOssClient1Application.class, args);
    }
}
