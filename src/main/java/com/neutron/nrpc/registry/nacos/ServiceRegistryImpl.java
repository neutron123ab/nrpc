package com.neutron.nrpc.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.neutron.nrpc.config.NRpcRegisterConfig;
import com.neutron.nrpc.registry.ServiceRegistry;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.Properties;

/**
 * 服务注册
 * 
 * @author zzs
 * @date 2023/8/28 1:05
 */
public class ServiceRegistryImpl implements ServiceRegistry {
    
    @Resource
    private NRpcRegisterConfig nRpcRegisterConfig;
    
    @Override
    public void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        Properties properties = new Properties();
        properties.setProperty("serveAddr", nRpcRegisterConfig.getServerAddr());
        try {
            NamingService namingService = NamingFactory.createNamingService(properties);
            namingService.registerInstance(serviceName, inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}
