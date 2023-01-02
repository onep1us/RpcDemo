package client.net;

import common.model.RpcRequest;
import common.model.RpcResponse;

/**
 * @author wanjiahao
 */
public interface RpcClient {
    /**
     * 远程调用方法发送给服务器端的请求
     * @param rpcRequest
     * @return RpcResponse
     */
    RpcResponse sendRequest(RpcRequest rpcRequest);
}
