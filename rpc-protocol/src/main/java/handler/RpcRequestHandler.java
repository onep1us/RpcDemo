package handler;

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
        log.info("rpcRequest : {}", rpcRequest);
        if (header.getStatus() == MsgStatusEnum.SUCCESS.getCode()) {
            Object service = serviceMap.getOrDefault(rpcRequest.getInterfaceName(),null);
            log.info("获取到了服务");
            if(null == service){
                //todo 抛出异常
                return;
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
}

