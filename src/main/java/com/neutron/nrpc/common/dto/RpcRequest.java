package com.neutron.nrpc.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zzs
 * @date 2023/8/7 23:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest {

    /**
     * 请求id
     */
    private String requestId;

    /**
     * 接口名
     */
    private String interfaceName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法参数列表
     */
    private Object[] parameters;

    /**
     * 方法参数类型
     */
    private Class<?> paramTypes;

    /**
     * 版本号
     */
    private String version;

    /**
     * 群组
     */
    private String group;
    
    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }

}
