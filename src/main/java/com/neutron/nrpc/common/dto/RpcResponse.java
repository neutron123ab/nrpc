package com.neutron.nrpc.common.dto;

import com.neutron.nrpc.common.enums.RpcResponseCodeEnum;
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
     * 响应码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应体
     */
    private T data;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setMessage(RpcResponseCodeEnum.SUCCESS.getMessage());
        rpcResponse.setCode(RpcResponseCodeEnum.SUCCESS.getCode());
        if (data != null) {
            rpcResponse.setData(data);
        }
        return rpcResponse;
    }

    public static <T> RpcResponse<T> fail(String requestId) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setCode(RpcResponseCodeEnum.FAIL.getCode());
        rpcResponse.setMessage(RpcResponseCodeEnum.FAIL.getMessage());
        return rpcResponse;
    }
}
