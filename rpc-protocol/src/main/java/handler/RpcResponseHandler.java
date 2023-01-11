package handler;

import common.UnprocessedRequests;
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

    private final UnprocessedRequests unprocessedRequests;

    public RpcResponseHandler() {
        unprocessedRequests = UnprocessedRequests.getInstance();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> rpcProtocol) throws Exception {
        try {
            log.info(String.format("client receive msg: %s", rpcProtocol));
            unprocessedRequests.complete(rpcProtocol);
        } finally {
            ReferenceCountUtil.release(rpcProtocol);
        }
    }
}
