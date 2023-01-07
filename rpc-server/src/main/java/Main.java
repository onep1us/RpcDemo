import server.net.NettyServer;
import server.net.RpcServer;
import server.register.NacosServiceRegistry;

public class Main {
    public static void main(String[] args) {
        RpcServer rpcServer = new NettyServer("127.0.0.1",8000,new NacosServiceRegistry("123.60.148.100:8848"));
        rpcServer.register(new HelloServiceImpl());
        rpcServer.start();
    }
}
