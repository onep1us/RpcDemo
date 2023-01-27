package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wanjiahao
 */
@Getter
@AllArgsConstructor
public enum RpcErrorEnum {
    /**
     * 注册的服务没有实现接口
     */
    NOT_IMPLEMENT_INTERFACE("服务没有实现接口"),

    /**
     * 没有找到对应的服务
     */
    SERVER_SERVICE_NOT_FOUND("服务端本地没有找到对应的服务"),
    /**
     * 没有找到消息类型
     */
    MESSAGE_TYPE_NOT_FOUND("没有找到消息类型"),
    /**
     * 建立连接失败
     */
    CONNECT_ERROR("建立连接失败"),
    /**
     * 未知
     */
    UNKNOWN("未知错误")
    ;
    private String message;
}
