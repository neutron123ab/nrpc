package com.neutron.nrpc.common.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取单例对象的工厂类
 *
 * @author zzs
 * @date 2023/8/9 23:22
 */
public final class SingletonFactory {

    private static final Map<String, Object> SINGLETON_INSTANCE_MAP = new ConcurrentHashMap<>();

    private SingletonFactory() {}

    public static <T> T getSingletonInstance(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        String key = clazz.toString();
        Object instance = SINGLETON_INSTANCE_MAP.computeIfAbsent(key, t -> {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
        return clazz.cast(instance);
    }

}
