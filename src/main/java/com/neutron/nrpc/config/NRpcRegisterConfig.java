package com.neutron.nrpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 用户启动springboot服务时自动扫描配置文件并注入属性（注册中心）
 * @author zzs
 * @date 2023/8/8 0:54
 */
@Configuration
@ConfigurationProperties("spring.cloud.nacos.discovery")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ComponentScan
public class NRpcRegisterConfig {

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * nacos地址
     */
    private String serverAddr;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    @Bean
    public NRpcRegisterConfig nRpcRegisterConfig() {
        return new NRpcRegisterConfig(namespace, serverAddr, username, password);
    }

}
