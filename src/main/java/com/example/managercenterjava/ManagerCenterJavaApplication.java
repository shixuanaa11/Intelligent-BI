package com.example.managercenterjava;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.managercenterjava.mapper")
public class ManagerCenterJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManagerCenterJavaApplication.class, args);
    }

}
