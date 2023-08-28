package com.neutron.nrpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zzs
 * @date 2023/8/29 1:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NRpcServiceConfig {
    
    private String version = "";
    
    private String group = "";
    
    private Object service;

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
    
}
