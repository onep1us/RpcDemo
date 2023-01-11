package client.discovery;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wanjiahao
 */
public interface RpcDiscovery {

    Map<String,List<InetSocketAddress>> serviceMap = new ConcurrentHashMap<>();
    /**
     * 服务发现
     * @param serviceName
     * @return
     */
    List<InetSocketAddress> lookupService(String serviceName);
}
