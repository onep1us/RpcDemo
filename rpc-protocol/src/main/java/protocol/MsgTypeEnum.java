package protocol;

import lombok.Getter;

/**
 * @author wanjiahao
 */

public enum MsgTypeEnum {
    /**
     * 请求消息
     */
    REQUEST(1),
    /**
     * 回复消息
     */
    RESPONSE(2);

    @Getter
    private final int type;

    MsgTypeEnum(int type) {
        this.type = type;
    }

    public static MsgTypeEnum findByType(int type) {
        for (MsgTypeEnum msgType : MsgTypeEnum.values()) {
            if (msgType.getType() == type) {
                return msgType;
            }
        }
        return null;
    }
}

