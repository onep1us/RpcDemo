package handler;

import enums.RpcErrorEnum;
import exception.RpcException;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import model.RpcRequest;
import model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protocol.MsgHeader;
import protocol.MsgStatusEnum;
import protocol.MsgTypeEnum;
import protocol.RpcProtocol;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author wanjiahao
 */
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private final Map<String,Object> serviceMap;

    public RpcRequestHandler(Map<String,Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> msg) throws Exception {
        MsgHeader header = msg.getHeader();
        RpcRequest rpcRequest = msg.getBody();
        if (header.getStatus() == MsgStatusEnum.SUCCESS.getCode() && !header.isHeartTag()) {
            Object service = serviceMap.getOrDefault(rpcRequest.getInterfaceName(),null);
            if(null == service){
                throw new RpcException(RpcErrorEnum.SERVER_SERVICE_NOT_FOUND,"not found service :" +rpcRequest.getInterfaceName());
            }
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParaClass());
            log.info("rpcRequest.getPara().getClass() :{}",rpcRequest.getPara().getClass());
            Object data = method.invoke(service, rpcRequest.getPara());
            RpcProtocol<RpcResponse> rpcProtocol = new RpcProtocol<>();
            header.setMsgType((byte)MsgTypeEnum.RESPONSE.getType());
            rpcProtocol.setHeader(header);
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setData(data);
            rpcProtocol.setBody(rpcResponse);
            ctx.writeAndFlush(rpcProtocol);
        }
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("long time no heart message, disconnect ");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}

