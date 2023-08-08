package com.neutron.nrpc.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zzs
 * @date 2023/8/7 23:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> {
    /**
     * 请求id
     */
    private String requestId;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应体
     */
    private T data;
}
