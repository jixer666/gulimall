package com.ljx.ssoserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class GulimallOssServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallOssServerApplication.class, args);
    }
}
