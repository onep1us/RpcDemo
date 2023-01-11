package client.net;

import model.RpcRequest;
import model.RpcResponse;
import protocol.RpcProtocol;

import java.util.concurrent.CompletableFuture;

/**
 * @author wanjiahao
 */
public interface RpcClient {
    /**
     * 远程调用方法发送给服务器端的请求
     * @param rpcRequest
     * @return RpcResponse
     */
    CompletableFuture<RpcResponse> sendRequest(RpcProtocol<RpcRequest> rpcRequest);
}
