package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wanjiahao
 */
@Getter
@AllArgsConstructor
public enum SerializeExceptionEnum {
    /**
     * 没有找到对应的序列化器
     */
    SERIALIZATION_NOT_FOUND("没有找到对应的序列化器");

    private String message;
}
