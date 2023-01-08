package client.discovery;

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


/**
 * @author wanjiahao
 */
@Slf4j
public class NacosServiceDiscovery extends DefaultDiscovery{
    private final NamingService namingService;

    public NacosServiceDiscovery(String address) {
        this.namingService = createNamingService(address);
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
    public InetSocketAddress lookupService(String serviceName) {
        try {
            if(!serviceMap.containsKey(serviceName)){
                List<Instance> allInstances = namingService.getAllInstances(serviceName);
                if(CollectionUtils.isEmpty(allInstances)){
                    throw new RegistryException(RegistryErrorEnum.REGISTRY_SERVICE_NOT_FOUND,"service name:" + serviceName);
                }
                Instance instance = allInstances.get(0);
                serviceMap.put(serviceName,new InetSocketAddress(instance.getIp(),instance.getPort()));
            }
            return serviceMap.get(serviceName);
        } catch (NacosException e) {
            log.error("lookupService error, serviceName:{}",serviceName,e);
            return null;
        }
    }
}
