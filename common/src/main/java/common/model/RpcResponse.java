package common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author onep1us
 */
@Data
public class RpcResponse implements Serializable {
    /**
     * 响应详情信息
     */
    String detail;
    /**
     * 调用远程方法返回的结果
     */
    private Object data;
}
