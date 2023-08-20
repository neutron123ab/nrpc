package com.neutron.nrpc.transport.codec;

import com.neutron.nrpc.common.compress.gzip.GzipCompress;
import com.neutron.nrpc.common.constants.RpcConstants;
import com.neutron.nrpc.common.dto.RpcMessage;
import com.neutron.nrpc.common.enums.CompressTypeEnum;
import com.neutron.nrpc.utils.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 编码器
 *
 * @author zzs
 * @date 2023/8/13 22:46
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    private static final AtomicInteger REQUEST_ID = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf byteBuf) throws Exception {
        try {
            byteBuf.writeBytes(RpcConstants.MAGIC_NUMBER);
            byteBuf.writeByte(RpcConstants.VERSION);
            //预留保存消息长度的四个字节
            byteBuf.writerIndex(byteBuf.writerIndex() + 4);
            byte messageType = rpcMessage.getMessageType();
            byteBuf.writeByte(messageType);
            byteBuf.writeByte(rpcMessage.getSerializationType());
            byteBuf.writeByte(CompressTypeEnum.GZIP.getCode());
            byteBuf.writeInt(REQUEST_ID.incrementAndGet());

            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            //当消息为非心跳消息时设置消息体
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE
                    && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                bodyBytes = ProtostuffUtil.serialize(rpcMessage.getData());
                GzipCompress gzipCompress = new GzipCompress();
                bodyBytes = gzipCompress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }

            if (bodyBytes != null) {
                byteBuf.writeBytes(bodyBytes);
            }
            int writerIndex = byteBuf.writerIndex();
            byteBuf.writerIndex(writerIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            byteBuf.writeInt(fullLength);
            byteBuf.writerIndex(writerIndex);
        } catch (Exception e) {
            log.error("消息编码器异常", e);
        }

    }
}
