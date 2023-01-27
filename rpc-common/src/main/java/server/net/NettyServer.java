package server.net;

import annotation.RpcService;
import annotation.RpcServiceScan;
import codec.RpcDecoder;
import codec.RpcEncoder;
import common.util.ReflectUtil;
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
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import server.register.RpcRegister;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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
        scanService();
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
                                    .addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
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
            serviceMap.put(anInterface.getSimpleName(), service);
            rpcRegister.register(anInterface.getSimpleName(), new InetSocketAddress(host, port));
        }
    }

    private void register(Object service, String serviceName) {
        serviceMap.put(serviceName, service);
        rpcRegister.register(serviceName, new InetSocketAddress(host, port));
    }

    private void scanService() {
        String mainClassName = ReflectUtil.getStackTrace();
        log.info("scanServices: {}",mainClassName);
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(RpcServiceScan.class)) {
                log.error("lose @ServiceScan");
                throw new RpcException(RpcErrorEnum.UNKNOWN);
            }
        } catch (ClassNotFoundException e) {
            throw new RpcException(RpcErrorEnum.UNKNOWN);
        }
        String basePackage = startClass.getAnnotation(RpcServiceScan.class).basePackage();
        if("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz : classSet) {
            if(clazz.isAnnotationPresent(RpcService.class)) {
                String serviceName = clazz.getAnnotation(RpcService.class).name();
                Object obj;
                try {
                    obj = clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    log.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if("".equals(serviceName)) {
                    register(obj);
                } else {
                    register(obj, serviceName);
                }
            }
        }
    }


}