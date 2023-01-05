package protocol;

import lombok.Getter;

public enum MsgTypeEnum {
    REQUEST(1),
    RESPONSE(2),
    HEARTBEAT(3);

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

