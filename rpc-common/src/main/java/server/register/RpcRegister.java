package server.register;

import exception.RegistryException;

import java.net.InetSocketAddress;

/**
 * @author wanjiahao
 */
public interface RpcRegister {
    /**
     * 注册服务
     * @param service
     * @param inetSocketAddress
     * @throws RegistryException
     */
    void register(String service, InetSocketAddress inetSocketAddress) throws RegistryException;

    /**
     * 注销服务
     * @param serviceName
     * @param inetSocketAddress
     * @throws RegistryException
     */
    void unRegister(String serviceName, InetSocketAddress inetSocketAddress) throws RegistryException;
}
