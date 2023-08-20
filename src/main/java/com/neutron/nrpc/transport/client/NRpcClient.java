package com.neutron.nrpc.transport.client;

import com.neutron.nrpc.common.dto.RpcRequest;
import com.neutron.nrpc.transport.RpcRequestTransport;
import com.neutron.nrpc.transport.codec.RpcMessageDecoder;
import com.neutron.nrpc.transport.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author zzs
 * @date 2023/8/8 0:09
 */
@Slf4j
public class NRpcClient implements RpcRequestTransport {

    private final EventLoopGroup eventLoopGroup;

    private final Bootstrap bootstrap;

    public NRpcClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                //客户端连接等待时长
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //设置心跳
                        pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new RpcMessageEncoder());
                        pipeline.addLast(new RpcMessageDecoder());
                    }
                });

    }

    @Override
    public Object sendRpcRequest(RpcRequest nRpcRequest) {
        return null;
    }
}
