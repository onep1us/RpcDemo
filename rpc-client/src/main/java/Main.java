import client.discovery.NacosServiceDiscovery;
import client.net.ProxyFactory;
import client.net.SocketClient;
import protocol.ProtocolConstants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try {
//            ProxyFactory proxyFactory = new ProxyFactory(new SocketClient(new NacosServiceDiscovery("123.60.148.100:8848")));
//            HelloService helloService = proxyFactory.getProxy(HelloService.class);
//            System.out.println(helloService.hello("wanjiahao"));
            System.out.println(ProtocolConstants.MAGIC);
            byte[] bytes = new byte[10];
            bytes[0] = 0;
            bytes[1] = 16;
            bytes[2] = 1;
            bytes[3] = 1;
            bytes[4] = 1;
            bytes[8] = 1;
            bytes[9] = 1;
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("192.168.13.1",8000),5000);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
