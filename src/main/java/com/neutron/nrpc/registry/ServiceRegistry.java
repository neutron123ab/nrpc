package com.neutron.nrpc.registry;

import java.net.InetSocketAddress;

/**
 * @author zzs
 * @date 2023/8/28 1:01
 */
public interface ServiceRegistry {

    /**
     * 服务注册
     * 
     * @param serviceName 服务名称
     * @param inetSocketAddress 服务地址
     */
    void registerService(String serviceName, InetSocketAddress inetSocketAddress);
    
}
