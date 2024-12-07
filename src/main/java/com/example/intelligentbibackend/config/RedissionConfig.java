package com.example.intelligentbibackend.config;



import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;


@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissionConfig {

    private Integer port;
    private String host;
    private Integer database;



@Bean
   public RedissonClient getRedissonClient(){
       Config config = new Config();
//       这里由于我们现在还是单机，所以使用单机配置useSingleServer,集群多机就要useClusterServers
       config.useSingleServer().setDatabase(1).setAddress("redis://"+host+":"+port);

// or read config from file
//       config = Config.fromYAML(new File("config-file.yaml"));
       // Sync and Async API
       RedissonClient redisson = Redisson.create(config);
       return redisson;
   }


}
