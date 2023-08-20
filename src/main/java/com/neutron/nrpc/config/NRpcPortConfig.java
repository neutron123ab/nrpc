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
@ConfigurationProperties("nrpc")
public class NRpcPortConfig {

    /**
     * 服务端口号, 默认为0，即用户不指定端口时会让netty自动选择一个可用端口
     */
    private Integer port = 0;

    @Bean
    public NRpcPortConfig serverPortConfig() {
        return new NRpcPortConfig(port);
    }

}
