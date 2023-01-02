import client.discovery.NacosServiceDiscovery;
import client.net.ProxyFactory;
import client.net.SocketClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try {
            ProxyFactory proxyFactory = new ProxyFactory(new SocketClient(new NacosServiceDiscovery("123.60.148.100:8848")));
            HelloService helloService = proxyFactory.getProxy(HelloService.class);
            System.out.println(helloService.hello("wanjiahao"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
