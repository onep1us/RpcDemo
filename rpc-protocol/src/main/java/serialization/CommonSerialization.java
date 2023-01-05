package serialization;

/**
 * 通用的序列化反序列化接口
 *
 * @author ziyang
 */
public interface CommonSerialization {

    /**
     * 序列化
     * @param obj
     * @return
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @return
     */
    Object deserialize(byte[] bytes, Class<?> clazz);
}
