package com.neutron.nrpc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 序列化类型枚举
 * @author zzs
 * @date 2023/8/10 0:32
 */
@AllArgsConstructor
@Getter
public enum SerializationTypeEnum {

    PROTOSTUFF((byte) 0x01, "protostuff"),

    KRYO((byte) 0x02, "kryo");

    private final byte code;

    private final String serializationName;

    /**
     * 根据序列化编号获取名字
     * @param code 编号
     * @return 序列化名字
     */
    public static String getSerializationNameByCode(byte code) {
        for (SerializationTypeEnum value : SerializationTypeEnum.values()) {
            if (value.getCode() == code) {
                return value.getSerializationName();
            }
        }
        return null;
    }
}
