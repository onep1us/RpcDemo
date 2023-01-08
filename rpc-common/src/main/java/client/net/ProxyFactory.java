package client.net;

import model.RpcRequest;
import protocol.*;
import serialization.SerializationTypeEnum;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author wanjiahao
 */
public class ProxyFactory implements InvocationHandler {
    RpcClient rpcClient;

    public ProxyFactory(RpcClient rpcClient){
        this.rpcClient = rpcClient;
    }

    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcProtocol<RpcRequest> rpcProtocol = new RpcProtocol<>();
        MsgHeader header = new MsgHeader();
        header.setMagic(ProtocolConstants.MAGIC);
        header.setMsgType((byte)MsgTypeEnum.REQUEST.getType());
        header.setSerialization(SerializationTypeEnum.KRYO.getType());
        header.setStatus(MsgStatusEnum.SUCCESS.getCode());
        RpcRequest rpcRequest = RpcRequest.builder().
                interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paraClass(method.getParameterTypes())
                .para(args).build();
        rpcProtocol.setHeader(header);
        rpcProtocol.setBody(rpcRequest);
        return rpcClient.sendRequest(rpcProtocol).getData();
    }
}
