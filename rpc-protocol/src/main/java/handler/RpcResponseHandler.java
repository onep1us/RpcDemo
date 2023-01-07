package handler;

import model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import protocol.RpcProtocol;

/**
 * @author wanjiahao
 */
@Slf4j
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> msg) throws Exception {
        try {
            log.info(String.format("client receive msg: %s", msg));
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            ctx.channel().attr(key).set(msg.getBody());
            ctx.channel().close();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
