package server.register;

import java.net.InetSocketAddress;

/**
 * @author wanjiahao
 */
public interface RpcRegister {
    /**
     * 服务注册
     * @param service
     * @param inetSocketAddress
     */
    void register(String service, InetSocketAddress inetSocketAddress);

    /**
     * 服务注销
     * @param serviceName
     * @param inetSocketAddress
     */
    void unRegister(String serviceName, InetSocketAddress inetSocketAddress);
}
