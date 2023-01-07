package serialization;

import lombok.Getter;

/**
 * @author wanjiahao
 */

public enum SerializationTypeEnum {
    /**
     * json序列化器
     */
    JSON(1),
    /**
     * kryo序列化器
     */
    KRYO(2);

    @Getter
    private final byte type;

    SerializationTypeEnum(int type) {
        this.type = (byte)type;
    }

    public static SerializationTypeEnum findByType(byte serializationType) {
        for (SerializationTypeEnum typeEnum : SerializationTypeEnum.values()) {
            if (typeEnum.getType() == serializationType) {
                return typeEnum;
            }
        }
        return JSON;
    }
}
