package com.neutron.nrpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zzs
 * @date 2023/8/13 22:10
 */
@Getter
@AllArgsConstructor
public enum RpcResponseCodeEnum {

    SUCCESS(200, "远程调用方法成功"),

    FAIL(500, "远程调用方法失败");

    private final Integer code;

    private final String message;

}
