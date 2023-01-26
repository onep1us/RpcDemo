package client.loadBalance;

import common.util.HashUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author wanjiahao
 */
@Slf4j
public class HashLoadBalancer implements LoadBalancer {
    @Override
    public InetSocketAddress select(List<InetSocketAddress> serviceAddresses, String serviceName) {
        Integer hash = HashUtil.FNV1_32_HASH(serviceName);
        return serviceAddresses.get(hash % serviceAddresses.size());
    }
}
