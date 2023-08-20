package com.neutron.nrpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zzs
 * @date 2023/8/11 0:41
 */
@AllArgsConstructor
@Getter
public enum CompressTypeEnum {

    GZIP((byte) 0x01, "gzip");

    private final byte code;

    private final String compressTypeName;

    public static String getNameByCode(byte code) {
        for (CompressTypeEnum value : CompressTypeEnum.values()) {
            if (value.getCode() == code) {
                return value.getCompressTypeName();
            }
        }
        return null;
    }


}
