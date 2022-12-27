package server.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
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
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            namingService.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e) {
            log.error("注册服务时有错误发生:", e);
            //todo 写一个异常类
//            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public void unRegister(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            namingService.deregisterInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e) {
            e.printStackTrace();
            //todo 写一个异常类
        }
    }
}
