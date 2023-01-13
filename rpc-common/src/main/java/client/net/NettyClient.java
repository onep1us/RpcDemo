package client.net;

import client.discovery.RpcDiscovery;
import client.loadBalance.LoadBalancer;
import client.loadBalance.RandomLoadBalancer;
import codec.RpcDecoder;
import codec.RpcEncoder;
import com.alibaba.nacos.common.utils.CollectionUtils;
import common.ChannelHolder;
import common.UnprocessedRequests;
import enums.RpcErrorEnum;
import exception.RpcException;
import io.netty.channel.Channel;
import lombok.SneakyThrows;
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
import lombok.extern.slf4j.Slf4j;
import protocol.RpcProtocol;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author wanjiahao
 */
@Slf4j
public class NettyClient implements RpcClient{
    private final RpcDiscovery rpcDiscovery;
    private final Bootstrap bootstrap;
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelHolder channelProvider;
    private  LoadBalancer loadBalancer;

    public NettyClient(RpcDiscovery rpcDiscovery) {
        this.unprocessedRequests = UnprocessedRequests.getInstance();
        this.loadBalancer = new RandomLoadBalancer();
        this.rpcDiscovery = rpcDiscovery;
        channelProvider = new ChannelHolder();
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
    public CompletableFuture<RpcResponse> sendRequest(RpcProtocol<RpcRequest> rpcProtocol) {
        try {
            log.info("sendRequest rpcProtocol :{}",rpcProtocol);
            RpcRequest rpcRequest = rpcProtocol.getBody();
            List<InetSocketAddress> inetSocketAddressList = rpcDiscovery.lookupService(rpcRequest.getInterfaceName());
            if(CollectionUtils.isEmpty(inetSocketAddressList)){
                return null;
            }
            CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
            unprocessedRequests.put(rpcProtocol.getHeader().getRequestId(), resultFuture);
            InetSocketAddress inetSocketAddress = loadBalancer.select(inetSocketAddressList,rpcRequest.getInterfaceName());
            Channel channel = getChannel(rpcRequest.getInterfaceName(),inetSocketAddress);
            channel.writeAndFlush(rpcProtocol);
            return resultFuture;
        } catch (Exception e) {
            log.error("sendRequest error,rpcProtocol:{}",rpcProtocol,e);
            return null;
        }
    }

    private Channel getChannel(String serviceName, InetSocketAddress inetSocketAddress){
        Channel channel = channelProvider.get(inetSocketAddress);
        if(null == channel){
            channel = connect(serviceName, inetSocketAddress);
        }
        return channel;
    }

    private Channel connect(String serviceName,InetSocketAddress inetSocketAddress){
        ChannelFuture channelFuture;
        try {
            channelFuture = bootstrap.connect(inetSocketAddress.getAddress(), inetSocketAddress.getPort()).sync();
        } catch (Exception e) {
            rpcDiscovery.removeCache(serviceName,inetSocketAddress);
            log.error("connect error, inetSocketAddress :{}",inetSocketAddress);
            throw new RpcException(RpcErrorEnum.CONNECT_ERROR);
        }
        return channelFuture.channel();
    }
}
