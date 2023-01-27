import api.HelloPara;
import api.HelloService;
import client.discovery.NacosServiceDiscovery;
import client.net.NettyClient;
import client.net.ProxyFactory;
import client.net.RpcClient;

public class Main {
    public static void main(String[] args) {
        try {
            RpcClient nettyClient = new NettyClient(new NacosServiceDiscovery("123.60.148.100:8848"));
            ProxyFactory proxyFactory = new ProxyFactory(nettyClient);
            HelloService helloService = proxyFactory.getProxy(HelloService.class);
            HelloPara helloPara = new HelloPara();
            helloPara.setA(1);
            helloPara.setC(1.0);
            helloPara.setB(1);
            System.out.println(helloService.hello("helloPara"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
