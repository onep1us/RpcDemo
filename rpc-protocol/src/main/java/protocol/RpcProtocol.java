package protocol;

import java.io.Serializable;

/**
 * @author wanjiahao
 */
public class RpcProtocol<T> implements Serializable {
    private MsgHeader head;
    private T body;
}
