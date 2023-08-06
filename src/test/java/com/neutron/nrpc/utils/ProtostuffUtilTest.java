package com.neutron.nrpc.utils;

import java.util.Arrays;

/**
 * @author zzs
 * @date 2023/8/6 23:09
 */
public class ProtostuffUtilTest {

    public static void main(String[] args) {
        Demo demo = new Demo();
        demo.setUsername("neutron");
        demo.setPassword("123456");
        System.out.println("序列化前的对象数据" + demo);

        byte[] bytes = ProtostuffUtil.serialize(demo);
        System.out.println("对象序列化后的字节数组：" + Arrays.toString(bytes));

        Demo deserialize = ProtostuffUtil.deserialize(bytes, Demo.class);
        System.out.println("反序列化后的数据：" + deserialize) ;

    }

}
