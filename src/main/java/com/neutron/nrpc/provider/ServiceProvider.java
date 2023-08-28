package com.neutron.nrpc.provider;

import com.neutron.nrpc.config.NRpcServiceConfig;

/**
 * @author zzs
 * @date 2023/8/29 1:21
 */
public interface ServiceProvider {
    
    void addService(NRpcServiceConfig nRpcServiceConfig);
    
    Object getService(String rpcServiceName);
    
    void publishService(NRpcServiceConfig nRpcServiceConfig);
    
}
