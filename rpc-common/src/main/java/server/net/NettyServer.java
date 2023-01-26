package server.net;

import codec.RpcDecoder;
import codec.RpcEncoder;
import common.util.ShutDownHook;
import enums.RpcErrorEnum;
import exception.RpcException;
import handler.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import server.register.RpcRegister;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wanjiahao
 */
@Slf4j
public class NettyServer implements RpcServer{

    private final int port;
    private final String host;
    private final RpcRegister rpcRegister;
    Map<String,Object> serviceMap = new ConcurrentHashMap<>();

    public NettyServer(String host,int port, RpcRegister rpcRegister) {
        this.host = host;
        this.port = port;
        this.rpcRegister = rpcRegister;
    }

    @Override
    public void start() {
        EventLoopGroup boss = null;
        EventLoopGroup worker = null;
        try {

            boss = new NioEventLoopGroup(1);
            worker = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new RpcDecoder())
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcRequestHandler(serviceMap));
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.bind(host, port).sync();

            ShutDownHook.addClearAllHook(this);
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e){
            log.error("netty error",e);
        } finally{
            if(null != boss) {
                boss.shutdownGracefully();
            }
            if(null != worker) {
                worker.shutdownGracefully();
            }
        }
    }

    @Override
    public void stop() {
        log.info("关闭后将自动注销所有服务");
        InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
        serviceMap.keySet()
                .forEach((serviceName) -> rpcRegister.unRegister(serviceName,inetSocketAddress));
    }

    @Override
    public void register(Object service){
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length == 0){
            log.error("server register service error, The service {} does not implement the interface ",service.getClass().getCanonicalName());
            throw new RpcException(RpcErrorEnum.NOT_IMPLEMENT_INTERFACE,"service name :" + service.getClass().getCanonicalName());
        }
        for (Class<?> anInterface : interfaces) {
            serviceMap.put(anInterface.getCanonicalName(), service);
            rpcRegister.register(anInterface.getCanonicalName(), new InetSocketAddress(host, port));
        }
    }
}