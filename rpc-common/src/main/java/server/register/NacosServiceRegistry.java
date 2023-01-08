package server.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import enums.RegistryErrorEnum;
import exception.RegistryException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author wanjiahao
 */
@Slf4j
public class NacosServiceRegistry implements RpcRegister{

    private final NamingService namingService;

    public NacosServiceRegistry(String address) {
            this.namingService = createNamingService(address);
    }

    public static NamingService createNamingService(String address) {
        try {
            return NamingFactory.createNamingService(address);
        } catch (NacosException e) {
            log.error("nacos createNamingService error: ", e);
            return null;
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) throws RegistryException{
        try {
            namingService.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e) {
            log.error("nacos register service error:", e);
            throw new RegistryException(RegistryErrorEnum.REGISTER_SERVICE_FAILURE,"service name:" + serviceName);
        }
    }

    @Override
    public void unRegister(String serviceName, InetSocketAddress inetSocketAddress) throws RegistryException{
        try {
            namingService.deregisterInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e) {
            log.error("nacos unregister service error");
            throw new RegistryException(RegistryErrorEnum.UNREGISTER_SERVICE_FAILURE,"service name:" + serviceName);
        }
    }
}
