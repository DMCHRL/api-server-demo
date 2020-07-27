package com.connectiontech.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.connectiontech.demo.mapper")
public class ApiServerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiServerDemoApplication.class, args);
    }

}
