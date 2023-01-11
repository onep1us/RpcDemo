package common;

import model.RpcResponse;
import protocol.RpcProtocol;
import serialization.JsonSerialization;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wanjiahao
 */
public class UnprocessedRequests {

    private final Map<Long, CompletableFuture<RpcResponse>> unprocessedRequestsMap;

    private static volatile UnprocessedRequests unprocessedRequests;

    private UnprocessedRequests(){
        this.unprocessedRequestsMap = new ConcurrentHashMap<>();
    }

    public void put(Long requestId, CompletableFuture<RpcResponse> future) {
        unprocessedRequestsMap.put(requestId, future);
    }

    public void complete(RpcProtocol<RpcResponse> rpcProtocol) {
        CompletableFuture<RpcResponse> future = unprocessedRequestsMap.remove(rpcProtocol.getHeader().getRequestId());
        if (null != future) {
            future.complete(rpcProtocol.getBody());
        } else {
            throw new IllegalStateException();
        }
    }

    public static UnprocessedRequests getInstance(){
        if(null == unprocessedRequests){
            synchronized (JsonSerialization.class){
                if(null == unprocessedRequests){
                    unprocessedRequests = new UnprocessedRequests();
                }
            }
        }
        return unprocessedRequests;
    }
}
