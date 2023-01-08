package client.net;

import client.discovery.RpcDiscovery;
import codec.RpcDecoder;
import codec.RpcEncoder;
import exception.RegistryException;
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

/**
 * @author wanjiahao
 */
@Slf4j
public class NettyClient implements RpcClient{
    RpcDiscovery rpcDiscovery;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public NettyClient(RpcDiscovery rpcDiscovery) {
        this.rpcDiscovery = rpcDiscovery;
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
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
    @Override
    public RpcResponse sendRequest(RpcProtocol<RpcRequest> rpcProtocol) {
        try {
            log.info("sendRequest rpcProtocol :{}",rpcProtocol);
            RpcRequest rpcRequest = rpcProtocol.getBody();
            InetSocketAddress inetSocketAddress = rpcDiscovery.lookupService(rpcRequest.getInterfaceName());
            if(null == inetSocketAddress){
                return null;
            }
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
