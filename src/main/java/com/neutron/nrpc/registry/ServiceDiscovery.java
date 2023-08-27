package com.neutron.nrpc.registry;

import com.neutron.nrpc.common.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @author zzs
 * @date 2023/8/28 0:59
 */
public interface ServiceDiscovery {

    /**
     * 根据服务名发现服务地址
     * 
     * @param rpcRequest rpc请求实体
     * @return 服务地址
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);
    
}
