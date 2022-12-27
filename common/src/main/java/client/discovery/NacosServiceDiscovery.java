package client.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.common.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Collections;
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
            log.error("连接到Nacos时有错误发生: ", e);
            //todo 写一个异常类
            return null;
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            if(!serviceMap.containsKey(serviceName)){
                List<Instance> allInstances = namingService.getAllInstances(serviceName);
                if(CollectionUtils.isEmpty(allInstances)){
                    //todo 抛出异常
                    return null;
                }
                Instance instance = allInstances.get(0);
                serviceMap.put(serviceName,new InetSocketAddress(instance.getIp(),instance.getPort()));
            }
            return serviceMap.get(serviceName);
        } catch (NacosException e) {
            e.printStackTrace();
            return null;
        }
    }
}
