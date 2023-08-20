package com.neutron.nrpc.common.constants;


/**
 * @author zzs
 * @date 2023/8/10 0:41
 */
public final class RpcConstants {

    private RpcConstants() {}

    /**
     * 魔数，四个字节，用于在服务端接收数据时判断数据包是否符合规范，关闭不遵守自定义协议的连接，节省资源
     */
    public static final byte[] MAGIC_NUMBER = {(byte) 'n', (byte) 'r', (byte) 'p', (byte) 'c'};

    public static final byte VERSION = 1;

    public static final byte REQUEST_TYPE = 1;

    public static final byte RESPONSE_TYPE = 2;

    /**
     * 消息头长度为16字节
     */
    public static final int HEAD_LENGTH = 16;

    /**
     * 心跳消息：ping
     */
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;

    /**
     * 心跳消息：pong
     */
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;

    public static final String PING = "ping";

    public static final String PONG = "pong";

    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

}
