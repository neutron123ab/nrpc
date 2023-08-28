package com.neutron.nrpc.transport.handler;

import com.neutron.nrpc.common.dto.RpcRequest;
import com.neutron.nrpc.common.factory.SingletonFactory;
import com.neutron.nrpc.provider.ServiceProvider;
import com.neutron.nrpc.provider.impl.NacosServiceProviderImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zzs
 * @date 2023/8/29 1:19
 */
public class RpcRequestHandler {
    
    private final ServiceProvider serviceProvider;
    
    public RpcRequestHandler() {
        serviceProvider = SingletonFactory.getSingletonInstance(NacosServiceProviderImpl.class);
    }
    
    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }
    
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    
}
