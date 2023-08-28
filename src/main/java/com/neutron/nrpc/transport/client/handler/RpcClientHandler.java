package com.neutron.nrpc.transport.client.handler;

import com.neutron.nrpc.common.constants.RpcConstants;
import com.neutron.nrpc.common.dto.RpcMessage;
import com.neutron.nrpc.common.dto.RpcResponse;
import com.neutron.nrpc.common.enums.CompressTypeEnum;
import com.neutron.nrpc.common.enums.SerializationTypeEnum;
import com.neutron.nrpc.common.factory.SingletonFactory;
import com.neutron.nrpc.transport.client.NRpcClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;

/**
 * @author zzs
 * @date 2023/8/28 0:34
 */
public class RpcClientHandler extends ChannelInboundHandlerAdapter {
    
    private final UnprocessedRequest unprocessedRequest;
    
    private final NRpcClient nRpcClient;
    
    public RpcClientHandler() {
        this.unprocessedRequest = SingletonFactory.getSingletonInstance(UnprocessedRequest.class);
        this.nRpcClient = SingletonFactory.getSingletonInstance(NRpcClient.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcMessage) {
                RpcMessage tmp = (RpcMessage) msg;
                byte messageType = tmp.getMessageType();
                if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                    
                } else {
                    RpcResponse<Object> rpcResponse = (RpcResponse<Object>) tmp.getData();
                    unprocessedRequest.complete(rpcResponse);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                Channel channel = nRpcClient.getChannel((InetSocketAddress) ctx.channel().remoteAddress());
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setSerializationType(SerializationTypeEnum.PROTOSTUFF.getCode());
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                rpcMessage.setMessageType(RpcConstants.HEARTBEAT_REQUEST_TYPE);
                rpcMessage.setData(RpcConstants.PING);
                channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);   
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
