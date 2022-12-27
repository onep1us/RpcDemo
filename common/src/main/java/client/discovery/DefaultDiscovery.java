package client.discovery;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wanjiahao
 *
 */
public class DefaultDiscovery implements RpcDiscovery{
    Map<String,InetSocketAddress> serviceMap = new ConcurrentHashMap<>();

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        return serviceMap.getOrDefault(serviceName,null);
    }
}
