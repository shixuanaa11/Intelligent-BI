package com.example.intelligentbibackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.intelligentbibackend.mapper")
public class IntelligentBIApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelligentBIApplication.class, args);
    }

}
