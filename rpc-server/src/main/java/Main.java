import server.net.SocketServer;
import server.register.NacosServiceRegistry;

public class Main {
    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer("127.0.0.1",8000,new NacosServiceRegistry("123.60.148.100:8848"));
        socketServer.register(new HelloServiceImpl());
        socketServer.start();
    }
}
