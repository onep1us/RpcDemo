package protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wanjiahao
 */
@Data
public class RpcProtocol<T> implements Serializable {
    private MsgHeader header;
    private T body;
}
