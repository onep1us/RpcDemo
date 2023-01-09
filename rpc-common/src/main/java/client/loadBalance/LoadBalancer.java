package client.loadBalance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author wanjiahao
 */
public interface LoadBalancer {

    /**
     * 负载均衡选择实例
     * @param inetSocketAddressList
     * @param serviceName
     * @return
     */
    //todo 实现一致性哈希负载均衡
    InetSocketAddress select(List<InetSocketAddress> inetSocketAddressList, String serviceName);
}
