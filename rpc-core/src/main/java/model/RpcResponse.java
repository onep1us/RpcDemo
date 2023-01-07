package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author onep1us
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
