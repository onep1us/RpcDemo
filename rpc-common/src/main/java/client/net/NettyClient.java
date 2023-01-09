package client.net;

import client.discovery.RpcDiscovery;
import client.loadBalance.LoadBalancer;
import client.loadBalance.RandomLoadBalancer;
import codec.RpcDecoder;
import codec.RpcEncoder;
import com.alibaba.nacos.common.utils.CollectionUtils;
import model.RpcRequest;
import model.RpcResponse;
import handler.RpcResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import protocol.RpcProtocol;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author wanjiahao
 */
@Slf4j
public class NettyClient implements RpcClient{
    private final RpcDiscovery rpcDiscovery;
    private final Bootstrap bootstrap;
    private LoadBalancer loadBalancer;

    public NettyClient(RpcDiscovery rpcDiscovery) {
        this.loadBalancer = new RandomLoadBalancer();
        this.rpcDiscovery = rpcDiscovery;
        bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new RpcEncoder())
                                .addLast(new RpcDecoder())
                                .addLast(new RpcResponseHandler());
                    }
                });
    }

    public NettyClient(RpcDiscovery rpcDiscovery,LoadBalancer loadBalancer) {
        this(rpcDiscovery);
        this.loadBalancer = loadBalancer;
    }

    @Override
    public RpcResponse sendRequest(RpcProtocol<RpcRequest> rpcProtocol) {
        try {
            log.info("sendRequest rpcProtocol :{}",rpcProtocol);
            RpcRequest rpcRequest = rpcProtocol.getBody();
            List<InetSocketAddress> inetSocketAddressList = rpcDiscovery.lookupService(rpcRequest.getInterfaceName());
            if(CollectionUtils.isEmpty(inetSocketAddressList)){
                return null;
            }
            //todo 本地缓存失效解决
            //todo 写一个本地缓存缓存channel
            //todo 异步返回结果
            InetSocketAddress inetSocketAddress = loadBalancer.select(inetSocketAddressList,rpcRequest.getInterfaceName());
            ChannelFuture channelFuture = bootstrap.connect(inetSocketAddress.getAddress(), inetSocketAddress.getPort()).sync();
            channelFuture.channel().writeAndFlush(rpcProtocol);
            channelFuture.channel().closeFuture().sync();
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            return channelFuture.channel().attr(key).get();
        } catch (Exception e) {
            log.error("sendRequest error,rpcProtocol:{}",rpcProtocol,e);
            return null;
        }
    }
}
