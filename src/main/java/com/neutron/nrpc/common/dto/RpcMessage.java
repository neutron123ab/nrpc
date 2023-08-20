package com.neutron.nrpc.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zzs
 * @date 2023/8/7 23:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcMessage {

    /**
     * 消息类型，如：请求、响应、心跳等
     */
    private byte messageType;

    /**
     * 序列化类型（protostuff、kryo）
     */
    private byte serializationType;

    /**
     * 压缩类型
     */
    private byte compress;

    /**
     * 请求id
     */
    private int requestId;

    /**
     * 请求数据
     */
    private Object data;

}
