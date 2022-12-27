package client.discovery;

import java.net.InetSocketAddress;

/**
 * @author wanjiahao
 */
public interface RpcDiscovery {
    /**
     * 服务发现
     * @param serviceName
     * @return
     */
    InetSocketAddress lookupService(String serviceName);
}
