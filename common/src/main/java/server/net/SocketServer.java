package server.net;

import common.model.RpcRequest;
import common.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import server.register.RpcRegister;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author wanjiahao
 */
@Slf4j
public class SocketServer implements RpcServer{
    private final String host;
    private final int port;
    private final RpcRegister rpcRegister;
    private final ExecutorService threadPool;
    Map<String,Object> serviceMap = new ConcurrentHashMap<>();

    public SocketServer(String host,int port, RpcRegister rpcRegister) {
        this.host = host;
        this.port = port;
        this.rpcRegister = rpcRegister;
        int corePoolSize = 5;
        int maximumPoolSize = 20;
        long keepAliveTime = 60;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    @Override
    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(port));
            Socket socket;
            log.info("rpc服务端启动，等待连接");
            while((socket = serverSocket.accept()) != null){
                threadPool.execute(new WorkThread(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void register(Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length == 0){
            //todo 抛出异常
            return;
        }
        for (Class<?> anInterface : interfaces) {
            serviceMap.put(anInterface.getCanonicalName(), service);
            rpcRegister.register(anInterface.getCanonicalName(), new InetSocketAddress(host, port));
        }
    }

    private class WorkThread implements Runnable{
        Socket socket;

        public WorkThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                log.info("连接成功");
                while (true) {
                    RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
                    log.info("server receive rpcRequest: {}", rpcRequest);
                    Object service = getService(rpcRequest.getInterfaceName());
                    Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParaClass());
                    Object data = method.invoke(service, rpcRequest.getPara());
                    RpcResponse rpcResponse = new RpcResponse();
                    rpcResponse.setData(data);
                    objectOutputStream.writeObject(rpcResponse);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private Object getService(String interfaceName) {
        return serviceMap.getOrDefault(interfaceName,"null");
    }
}
