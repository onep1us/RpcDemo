package client.loadBalance;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wanjiahao
 */
public class RoundRobinLoadBalancer implements LoadBalancer{

    private final Map<String,AtomicInteger> map = new ConcurrentHashMap<>();
    @Override
    public InetSocketAddress select(List<InetSocketAddress> inetSocketAddressList, String serviceName) {
        if(!map.containsKey(serviceName)){
            map.put(serviceName,new AtomicInteger(0));
        }
        AtomicInteger index = map.get(serviceName);
        if(index.get() >= inetSocketAddressList.size()) {
            index.getAndSet(index.get() % inetSocketAddressList.size());
        }
        int next = index.addAndGet(1);
        return inetSocketAddressList.get(next);
    }
}
