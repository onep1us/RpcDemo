package server.net;

/**
 * @author wanjiahao
 */
public interface RpcServer {

    /**
     * rpc服务端启动
     */
    void start();

    /**
     * rpc服务端停止
     */
    void stop();

    /**
     * 服务注册
     * @param service
     */
    void register(Object service);
}
