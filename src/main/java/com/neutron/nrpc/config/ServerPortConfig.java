package com.neutron.nrpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zzs
 * @date 2023/8/9 0:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ComponentScan
@Configuration
@ConfigurationProperties("server")
public class ServerPortConfig {

    /**
     * 服务端口号, 默认为8080
     */
    private Integer port = 8080;

    @Bean
    public ServerPortConfig serverPortConfig() {
        return new ServerPortConfig(port);
    }

}
