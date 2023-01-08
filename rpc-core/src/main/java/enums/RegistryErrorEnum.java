package enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wanjiahao
 */
@Getter
@AllArgsConstructor
public enum RegistryErrorEnum {
    /**
     * 注册服务失败
     */
    REGISTER_SERVICE_FAILURE("注册服务失败"),
    /**
     * 注销服务失败
     */
    UNREGISTER_SERVICE_FAILURE("注销服务失败"),
    /**
     * 连接注册中心失败
     */
    CONNECT_REGISTRY_FAILURE("连接注册中心失败"),
    /**
     * 未找到服务
     */
    REGISTRY_SERVICE_NOT_FOUND("注册中心未找到服务");

    private String message;
}
