package client.loadBalance;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

/**
 * @author wanjiahao
 */
public class RandomLoadBalancer implements LoadBalancer{
    @Override
    public InetSocketAddress select(List<InetSocketAddress> serviceAddresses,String serviceName) {
        return serviceAddresses.get(new Random().nextInt(serviceAddresses.size()));
    }
}
