package client.discovery;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wanjiahao
 *
 */
public class DefaultDiscovery implements RpcDiscovery{
    Map<String,List<InetSocketAddress>> serviceMap = new ConcurrentHashMap<>();

    @Override
    public List<InetSocketAddress> lookupService(String serviceName) {
        return serviceMap.getOrDefault(serviceName,null);
    }
}
