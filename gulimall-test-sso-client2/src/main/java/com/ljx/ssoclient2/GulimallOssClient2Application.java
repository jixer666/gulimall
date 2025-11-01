package com.ljx.ssoclient2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class GulimallOssClient2Application {
    public static void main(String[] args) {
        SpringApplication.run(GulimallOssClient2Application.class, args);
    }
}
