package exception;

import enums.RegistryErrorEnum;
import enums.RpcErrorEnum;

/**
 * @author wanjiahao
 */
public class RpcException extends RuntimeException{
    public RpcException(RpcErrorEnum rpcErrorEnum, String detail) {
        super(rpcErrorEnum.getMessage() + ":" + detail);
    }
    public RpcException(RpcErrorEnum rpcErrorEnum) {
        super(rpcErrorEnum.getMessage());
    }
}
