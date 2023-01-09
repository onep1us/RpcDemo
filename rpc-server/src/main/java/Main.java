import server.net.NettyServer;
import server.net.RpcServer;
import server.register.NacosServiceRegistry;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        RpcServer rpcServer = new NettyServer("127.0.0.1", 8000, new NacosServiceRegistry("123.60.148.100:8848"));
        rpcServer.register(new HelloServiceImpl());
        rpcServer.start();
    }
//
//    public static void main(String[] args){
//        Callable<Integer> func = () -> {
//            System.out.println("inside callable");
//            Thread.sleep(1000);
//            return 8;
//        };
//        FutureTask<Integer> futureTask  = new FutureTask<>(func);
//        Thread newThread = new Thread(futureTask);
//        newThread.start();
//
//        try {
//            System.out.println("blocking here");
//            Integer result = futureTask.get();
//            System.out.println(result);
//        } catch (InterruptedException ignored) {
//        } catch (ExecutionException ignored) {
//        }
//    }
}
