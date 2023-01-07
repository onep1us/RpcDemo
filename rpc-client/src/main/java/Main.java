import client.discovery.NacosServiceDiscovery;
import client.net.NettyClient;
import client.net.ProxyFactory;
import client.net.RpcClient;
import client.net.SocketClient;
import model.RpcRequest;
import model.RpcResponse;
import protocol.ProtocolConstants;
import serialization.JsonSerialization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
            System.out.println(helloService.hello(helloPara));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
