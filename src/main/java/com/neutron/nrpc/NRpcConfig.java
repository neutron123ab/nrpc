package com.neutron.nrpc;

import com.neutron.nrpc.config.NRpcRegisterConfig;
import com.neutron.nrpc.config.ServerPortConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zzs
 * @date 2023/8/9 0:52
 */
@Configuration
@EnableConfigurationProperties({NRpcRegisterConfig.class, ServerPortConfig.class})
public class NRpcConfig {
}
