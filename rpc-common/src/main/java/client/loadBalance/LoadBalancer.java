package client.loadBalance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author wanjiahao
 */
public interface LoadBalancer {

    /**
     * 负载均衡选择实例
     * @param serviceAddresses
     * @param serviceName
     * @return
     */
    InetSocketAddress select(List<InetSocketAddress> serviceAddresses, String serviceName);
}
