package common.util;

import lombok.extern.slf4j.Slf4j;
import server.net.RpcServer;

/**
 * @author wanjiahao
 */
@Slf4j
public class ShutDownHook {
    public static void addClearAllHook(RpcServer rpcServer) {
        log.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(rpcServer::stop));
    }
}
