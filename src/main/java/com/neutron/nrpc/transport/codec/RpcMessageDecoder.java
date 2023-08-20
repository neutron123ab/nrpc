package com.neutron.nrpc.transport.codec;

import com.neutron.nrpc.common.compress.gzip.GzipCompress;
import com.neutron.nrpc.common.constants.RpcConstants;
import com.neutron.nrpc.common.dto.RpcMessage;
import com.neutron.nrpc.common.dto.RpcRequest;
import com.neutron.nrpc.common.dto.RpcResponse;
import com.neutron.nrpc.utils.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.protostuff.Rpc;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 解码器
 * @author zzs
 * @date 2023/8/13 22:46
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    public RpcMessageDecoder() {
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decode;
            if (frame.readableBytes() >= RpcConstants.HEAD_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("解码失败", e);
                } finally {
                    frame.release();
                }
            }
        }
        return decode;
    }

    /**
     * 帧解码，要按顺序读取byteBuf
     * magicNumber(4B), version(1B), length(4B), messageType(1B), serializationType(1B), compress(1B), requestId(4B)
     * @param frame 数据帧
     */
    private Object decodeFrame(ByteBuf frame) {
        checkMagicNumber(frame);
        checkVersion(frame);
        //包括消息体在内的完整长度
        int fullLength = frame.readInt();
        byte messageType = frame.readByte();
        byte serializationType = frame.readByte();
        byte compress = frame.readByte();
        int requestId = frame.readInt();
        RpcMessage rpcMessage = new RpcMessage();
        rpcMessage.setMessageType(messageType);
        rpcMessage.setRequestId(requestId);
        rpcMessage.setSerializationType(serializationType);

        //根据不同的请求类型类设置消息体
        //心跳请求
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        //心跳响应
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        //计算出消息体的长度
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            //消息体
            byte[] body = new byte[bodyLength];
            frame.readBytes(body);
            //对消息体进行解压
            GzipCompress gzipCompress = new GzipCompress();
            body = gzipCompress.decompress(body);
            //反序列化解压对象
            if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest request = ProtostuffUtil.deserialize(body, RpcRequest.class);
                rpcMessage.setData(request);
            } else {
                RpcResponse response = ProtostuffUtil.deserialize(body, RpcResponse.class);
                rpcMessage.setData(response);
            }
        }
        return rpcMessage;
    }

    private void checkMagicNumber(ByteBuf frame) {
        byte[] magicNumber = new byte[RpcConstants.MAGIC_NUMBER.length];
        frame.readBytes(magicNumber);
        for (int i = 0; i < magicNumber.length; i++) {
            if (magicNumber[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("魔数错误, magicNumber = " + Arrays.toString(magicNumber));
            }
        }
    }

    private void checkVersion(ByteBuf frame) {
        byte version = frame.readByte();
        if (version != RpcConstants.VERSION) {
            throw new IllegalArgumentException("版本不兼容, version = " + version);
        }
    }
}
