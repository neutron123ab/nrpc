package com.neutron.nrpc.transport.server.handler;

import com.neutron.nrpc.common.constants.RpcConstants;
import com.neutron.nrpc.common.dto.RpcMessage;
import com.neutron.nrpc.common.dto.RpcRequest;
import com.neutron.nrpc.common.dto.RpcResponse;
import com.neutron.nrpc.common.enums.CompressTypeEnum;
import com.neutron.nrpc.common.enums.SerializationTypeEnum;
import com.neutron.nrpc.common.factory.SingletonFactory;
import com.neutron.nrpc.transport.handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zzs
 * @date 2023/8/9 23:59
 */
@Slf4j
public class RpcServerHandler extends ChannelInboundHandlerAdapter {
    
    private final RpcRequestHandler rpcRequestHandler;
    
    public RpcServerHandler() {
        this.rpcRequestHandler = SingletonFactory.getSingletonInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcMessage) {
                log.info("netty服务端接收到消息：[{}]", msg);
                RpcMessage rpcMessage = (RpcMessage) msg;
                byte messageType = rpcMessage.getMessageType();

                rpcMessage.setSerializationType(SerializationTypeEnum.PROTOSTUFF.getCode());
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                //收到心跳请求
                if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
                    rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                    rpcMessage.setData(RpcConstants.PONG);
                } else {
                    //服务端只能收到心跳和请求类型的数据包，所以此处只能为请求类型
                    RpcRequest rpcRequest = (RpcRequest) rpcMessage.getData();
                    Object result = rpcRequestHandler.handle(rpcRequest);
                    log.info("服务端调用目标方法结果为：{}", result);
                    rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                    //判断目标通道是否可写
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        RpcResponse<Object> successResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                        rpcMessage.setData(successResponse);
                    } else {
                        RpcResponse<Object> failResponse = RpcResponse.fail(rpcRequest.getRequestId());
                        rpcMessage.setData(failResponse);
                    }
                }
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                //读空闲，关闭连接
                ctx.close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("发生异常，断开连接");
        cause.printStackTrace();
        ctx.close();
    }
}
