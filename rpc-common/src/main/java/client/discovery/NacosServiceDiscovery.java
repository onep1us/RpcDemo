package client.discovery;

import client.loadBalance.LoadBalancer;
import client.loadBalance.RandomLoadBalancer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.common.utils.CollectionUtils;
import enums.RegistryErrorEnum;
import exception.RegistryException;
import exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author wanjiahao
 */
@Slf4j
public class NacosServiceDiscovery extends DefaultDiscovery{
    private final NamingService namingService;
    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(String address) {
        this.namingService = createNamingService(address);
        this.loadBalancer = new RandomLoadBalancer();
    }

    public NacosServiceDiscovery(String address, LoadBalancer loadBalancer) {
        this.namingService = createNamingService(address);
        this.loadBalancer = loadBalancer;
    }

    public static NamingService createNamingService(String address){
        try {
            return NamingFactory.createNamingService(address);
        } catch (NacosException e) {
            log.error("connect nacos error, address : {}",address, e);
            throw new RegistryException(RegistryErrorEnum.CONNECT_REGISTRY_FAILURE,"");
        }
    }

    @Override
    public List<InetSocketAddress> lookupService(String serviceName) {
        try {
            if(!serviceMap.containsKey(serviceName)){
                List<Instance> allInstances = namingService.getAllInstances(serviceName);
                if(CollectionUtils.isEmpty(allInstances)){
                    throw new RegistryException(RegistryErrorEnum.REGISTRY_SERVICE_NOT_FOUND,"service name:" + serviceName);
                }
                List<InetSocketAddress> inetSocketAddressList = allInstances.stream().map(instance -> new InetSocketAddress(instance.getIp(), instance.getPort())).collect(Collectors.toList());
                serviceMap.put(serviceName,inetSocketAddressList);
            }
            return serviceMap.get(serviceName);
        } catch (NacosException e) {
            log.error("lookupService error, serviceName:{}",serviceName,e);
            return null;
        }
    }
}
