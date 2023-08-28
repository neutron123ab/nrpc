package com.neutron.nrpc.transport.client;

import com.neutron.nrpc.common.compress.Compress;
import com.neutron.nrpc.common.constants.RpcConstants;
import com.neutron.nrpc.common.dto.RpcMessage;
import com.neutron.nrpc.common.dto.RpcRequest;
import com.neutron.nrpc.common.dto.RpcResponse;
import com.neutron.nrpc.common.enums.CompressTypeEnum;
import com.neutron.nrpc.common.enums.SerializationTypeEnum;
import com.neutron.nrpc.registry.ServiceDiscovery;
import com.neutron.nrpc.transport.RpcRequestTransport;
import com.neutron.nrpc.transport.client.handler.ChannelProvider;
import com.neutron.nrpc.transport.client.handler.RpcClientHandler;
import com.neutron.nrpc.transport.client.handler.UnprocessedRequest;
import com.neutron.nrpc.transport.codec.RpcMessageDecoder;
import com.neutron.nrpc.transport.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zzs
 * @date 2023/8/8 0:09
 */
@Slf4j
public class NRpcClient implements RpcRequestTransport {

    private final EventLoopGroup eventLoopGroup;

    private final Bootstrap bootstrap;
    
    @Resource
    private ServiceDiscovery serviceDiscovery;
    
    private final ChannelProvider channelProvider = new ChannelProvider();
    
    private final UnprocessedRequest unprocessedRequest = new UnprocessedRequest();

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
                        pipeline.addLast(new RpcClientHandler());
                    }
                });

    }
    
    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    @Override
    public Object sendRpcRequest(RpcRequest nRpcRequest) {
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(nRpcRequest);
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            unprocessedRequest.put(nRpcRequest.getRequestId(), resultFuture);
            RpcMessage rpcMessage = new RpcMessage();
            rpcMessage.setMessageType(RpcConstants.REQUEST_TYPE);
            rpcMessage.setSerializationType(SerializationTypeEnum.PROTOSTUFF.getCode());
            rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
            rpcMessage.setData(nRpcRequest);
            
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                }
            });
            
        }
        return resultFuture;
    }
    
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }
}
