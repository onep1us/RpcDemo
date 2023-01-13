package client.discovery;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wanjiahao
 */
public interface RpcDiscovery {

    Map<String,List<InetSocketAddress>> SERVICE_MAP = new ConcurrentHashMap<>();
    /**
     * 服务发现
     * @param serviceName
     * @return
     */
    List<InetSocketAddress> lookupService(String serviceName);


    /**
     * 注销本地缓存
     * @param serviceName
     * @param inetSocketAddress
     */
    void removeCache(String serviceName,InetSocketAddress inetSocketAddress);
}
