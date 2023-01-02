package common.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wanjiahao
 */
@Builder
@Data
public class RpcRequest implements Serializable {
    /**
     * 接口名
     */
    String interfaceName;
    /**
     * 方法名
     */
    String methodName;
    /**
     * 方法参数
     */
    Object[] para;
    /**
     * 方法参数类型
     */
    Class<?>[] paraClass;
}
