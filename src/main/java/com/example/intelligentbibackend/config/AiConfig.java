package com.example.intelligentbibackend.config;

import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * apikey的配置类，key从平台获取
 */
@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AiConfig {

    private String apiKey;
    @Bean
    public ClientV4 getClientV4(){
        return new ClientV4.Builder(apiKey).networkConfig(300, 100, 100, 100, TimeUnit.SECONDS).build();
    }
}
