package handler;

import common.UnprocessedRequests;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import model.RpcRequest;
import model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import protocol.MsgHeader;
import protocol.MsgStatusEnum;
import protocol.MsgTypeEnum;
import protocol.ProtocolConstants;
import protocol.RpcProtocol;
import serialization.SerializationTypeEnum;

import java.net.InetAddress;

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

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("send heart message [{}]", ctx.channel().remoteAddress());
                Channel channel = ctx.channel();
                RpcProtocol<RpcRequest> rpcProtocol = new RpcProtocol<>();
                MsgHeader header = new MsgHeader();
                header.setMagic(ProtocolConstants.MAGIC);
                header.setMsgType((byte) MsgTypeEnum.REQUEST.getType());
                header.setSerialization(SerializationTypeEnum.KRYO.getType());
                header.setStatus(MsgStatusEnum.SUCCESS.getCode());
                header.setHeartTag(true);
                rpcProtocol.setHeader(header);
                RpcRequest rpcRequest = new RpcRequest();
                rpcProtocol.setBody(rpcRequest);
                channel.writeAndFlush(rpcProtocol).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
