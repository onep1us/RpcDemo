package client.discovery;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author wanjiahao
 */
public interface RpcDiscovery {
    /**
     * 服务发现
     * @param serviceName
     * @return
     */
    List<InetSocketAddress> lookupService(String serviceName);
}
