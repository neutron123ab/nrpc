package com.neutron.nrpc.provider.impl;

import com.neutron.nrpc.config.NRpcServiceConfig;
import com.neutron.nrpc.provider.ServiceProvider;
import com.neutron.nrpc.registry.ServiceRegistry;
import com.neutron.nrpc.registry.nacos.ServiceRegistryImpl;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zzs
 * @date 2023/8/29 1:27
 */
public class NacosServiceProviderImpl implements ServiceProvider {
    
    private final Map<String, Object> serviceMap;
    
    private final Set<String> registeredService;
    
    private final ServiceRegistry serviceRegistry;
    
    public NacosServiceProviderImpl() {
        this.serviceMap = new ConcurrentHashMap<>();
        this.registeredService = ConcurrentHashMap.newKeySet();
        this.serviceRegistry = new ServiceRegistryImpl();
    }
    
    @Override
    public void addService(NRpcServiceConfig nRpcServiceConfig) {
        String rpcServiceName = nRpcServiceConfig.getServiceName();
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName, nRpcServiceConfig.getService());
    }

    @Override
    public Object getService(String rpcServiceName) {

        return serviceMap.get(rpcServiceName); 
    }

    @Override
    public void publishService(NRpcServiceConfig nRpcServiceConfig) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.addService(nRpcServiceConfig);
            serviceRegistry.registerService(nRpcServiceConfig.getRpcServiceName(), new InetSocketAddress(host, 9998));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
