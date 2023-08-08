package com.neutron.nrpc.transport;

import com.neutron.nrpc.common.dto.RpcRequest;

/**
 * @author zzs
 * @date 2023/8/7 23:38
 */
public interface RpcRequestTransport {

    /**
     * 发送rpc请求并获取返回结果
     * @param nRpcRequest rpc请求体
     * @return 请求调用结果
     */
    Object sendRpcRequest(RpcRequest nRpcRequest);

}
