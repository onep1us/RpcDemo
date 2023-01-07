package protocol;

import lombok.Getter;

/**
 * @author wanjiahao
 */

public enum MsgStatusEnum {
    /**
     * 成功状态
     */
    SUCCESS(0),
    /**
     * 失败状态
     */
    FAIL(1);

    @Getter
    private final byte code;

    MsgStatusEnum(int code) {
        this.code = (byte)code;
    }
}
