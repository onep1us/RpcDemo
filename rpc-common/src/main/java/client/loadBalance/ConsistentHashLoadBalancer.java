package client.loadBalance;

import common.util.HashUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wanjiahao
 */
@Slf4j
public class ConsistentHashLoadBalancer implements LoadBalancer{
    private final ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    public InetSocketAddress select(List<InetSocketAddress> serviceAddresses, String serviceName) {
        int identityHashCode = System.identityHashCode(serviceAddresses);
        ConsistentHashSelector selector = selectors.getOrDefault(serviceName, null);
        if(null == selector || selector.identityHashCode != identityHashCode){
            selectors.put(serviceName, new ConsistentHashSelector(serviceAddresses, 4, identityHashCode));
            selector = selectors.get(serviceName);
        }
        return selector.select(serviceName);
    }

    static class ConsistentHashSelector{

        private final int identityHashCode;
        private final TreeMap<Integer, InetSocketAddress> virtualInvokers;

        public ConsistentHashSelector(List<InetSocketAddress> serviceAddresses, int replicaNumber, int identityHashCode){
            this.identityHashCode = identityHashCode;
            this.virtualInvokers = new TreeMap<>();

            for (InetSocketAddress serviceAddress : serviceAddresses) {
                for(int i = 0; i < replicaNumber; i++){
                    String virtualName = serviceAddress.toString() + i;
                    Integer hash = HashUtil.FNV1_32_HASH(virtualName);
                    virtualInvokers.put(hash, serviceAddress);
                }
            }
        }

        public InetSocketAddress select(String serviceName) {
            Integer hash = HashUtil.FNV1_32_HASH(serviceName);
            Map.Entry<Integer, InetSocketAddress> entry = virtualInvokers.tailMap(hash, true).firstEntry();

            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }

            return entry.getValue();
        }
    }
}
