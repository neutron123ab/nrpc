package com.neutron.nrpc.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.neutron.nrpc.common.dto.RpcRequest;
import com.neutron.nrpc.config.NRpcRegisterConfig;
import com.neutron.nrpc.registry.ServiceDiscovery;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Properties;

/**
 * 服务发现
 * 
 * @author zzs
 * @date 2023/8/28 1:04
 */
@Component
public class ServiceDiscoveryImpl implements ServiceDiscovery {
    
    @Resource
    private NRpcRegisterConfig nRpcRegisterConfig;
    
    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        Properties properties = new Properties();
        properties.setProperty("serveAddr", nRpcRegisterConfig.getServerAddr());
        try {
            NamingService namingService = NamingFactory.createNamingService(properties);
            List<Instance> instances = namingService.selectInstances(rpcRequest.getRpcServiceName(), true);
            // TODO: 实现负载均衡
            String ip = instances.get(0).getIp();
            int port = instances.get(0).getPort();
            return new InetSocketAddress(ip, port);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}
