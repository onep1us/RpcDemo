package client.net;

import common.model.RpcRequest;

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
        RpcRequest rpcRequest = RpcRequest.builder().
                interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paraClass(method.getParameterTypes())
                .para(args).build();
        return rpcClient.sendRequest(rpcRequest).getData();
    }
}
