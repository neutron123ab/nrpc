package com.neutron.nrpc.utils;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zzs
 * @date 2023/8/6 22:27
 */
public class ProtostuffUtil {
    //避免每次序列化都重新生成buffer空间
    private static LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    //缓存schema
    private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    /**
     * 将指定对象序列化为字节数组
     * @param object 对象
     * @return 字节数组
     * @param <T> 泛型
     */
    public static <T> byte[] serialize(T object) {
        Class<T> clazz = (Class<T>) object.getClass();
        Schema<T> schema = getSchema(clazz);
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(object, schema, buffer);
        } finally {
            buffer.clear();
        }
        return data;
    }

    /**
     * 将字节数组反序列化为对象
     * @param data 字节数组
     * @param clazz 类对象
     * @return 对象
     * @param <T> 泛型
     */
    public static <T> T deserialize(byte[] data, Class<T> clazz) {
        Schema<T> schema = getSchema(clazz);
        T object = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, object, schema);
        return object;
    }

    /**
     * 根据类对象从缓存中获取schema
     * @param clazz 类对象
     * @return 该类对象对应的schema
     * @param <T> 泛型
     */
    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.getSchema(clazz);
            if (schema != null) {
                schemaCache.put(clazz, schema);
            }
        }
        return schema;
    }

    private ProtostuffUtil() {}
}
