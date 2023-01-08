package exception;

import enums.RpcErrorEnum;
import enums.SerializeExceptionEnum;

/**
 * @author wanjiahao
 */
public class SerializeException extends RuntimeException{
    public SerializeException(SerializeExceptionEnum serializeExceptionEnum) {
        super(serializeExceptionEnum.getMessage());
    }
    public SerializeException(SerializeExceptionEnum serializeExceptionEnum,String detail) {
        super(serializeExceptionEnum.getMessage()+ ":" + detail);
    }

    public SerializeException(String detail) {
        super(detail);
    }
}

