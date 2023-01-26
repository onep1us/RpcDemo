package client.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.common.utils.CollectionUtils;
import enums.RegistryErrorEnum;
import exception.RegistryException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author wanjiahao
 */
@Slf4j
public class NacosServiceDiscovery implements RpcDiscovery{

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
    public List<InetSocketAddress> lookupService(String serviceName) {
        try {
            if(!SERVICE_MAP.containsKey(serviceName) || CollectionUtils.isEmpty(SERVICE_MAP.get(serviceName))){
                List<Instance> allInstances = namingService.getAllInstances(serviceName);
                if(CollectionUtils.isEmpty(allInstances)){
                    throw new RegistryException(RegistryErrorEnum.REGISTRY_SERVICE_NOT_FOUND,"service name:" + serviceName);
                }
                // 注册监听器
                namingService.subscribe(serviceName, (event) ->
                {
                    if (event instanceof NamingEvent)
                    {
                        log.info("监听服务");
                        List<Instance> instances = ((NamingEvent) event).getInstances();
                        List<InetSocketAddress> inetSocketAddressList = new CopyOnWriteArrayList<>();
                        instances.forEach(instance -> inetSocketAddressList.add(new InetSocketAddress(instance.getIp(), instance.getPort())));
                        SERVICE_MAP.put(serviceName,inetSocketAddressList);
                    }
                });
                List<InetSocketAddress> inetSocketAddressList = new CopyOnWriteArrayList<>();
                allInstances.forEach(instance -> inetSocketAddressList.add(new InetSocketAddress(instance.getIp(), instance.getPort())));
                SERVICE_MAP.put(serviceName,inetSocketAddressList);
            }
            return SERVICE_MAP.get(serviceName);
        } catch (NacosException e) {
            log.error("lookupService error, serviceName:{}",serviceName,e);
            return null;
        }
    }

    @Override
    public void removeCache(String serviceName, InetSocketAddress inetSocketAddress) {
        List<InetSocketAddress> inetSocketAddressList = SERVICE_MAP.get(serviceName);
        if(CollectionUtils.isEmpty(inetSocketAddressList)){
            return;
        }
        inetSocketAddressList.removeIf(inetSocketAddress1 -> inetSocketAddress1.equals(inetSocketAddress));
    }
}
