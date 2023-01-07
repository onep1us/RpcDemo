package server.net;

import codec.RpcDecoder;
import codec.RpcEncoder;
import handler.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import server.register.RpcRegister;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wanjiahao
 */
public class NettyServer implements RpcServer{

    private final int port;
    private final String host;
    private final RpcRegister rpcRegister;
    private final ExecutorService threadPool;
    Map<String,Object> serviceMap = new ConcurrentHashMap<>();

    public NettyServer(String host,int port, RpcRegister rpcRegister) {
        this.host = host;
        this.port = port;
        this.rpcRegister = rpcRegister;
        int corePoolSize = 5;
        int maximumPoolSize = 20;
        long keepAliveTime = 60;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    @Override
    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {

            boss = new NioEventLoopGroup();
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
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e){
        } finally{
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void register(Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length == 0){
            //todo 抛出异常
            return;
        }
        for (Class<?> anInterface : interfaces) {
            serviceMap.put(anInterface.getCanonicalName(), service);
            rpcRegister.register(anInterface.getCanonicalName(), new InetSocketAddress(host, port));
        }
    }
}
