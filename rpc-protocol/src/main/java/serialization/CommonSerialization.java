package serialization;

import java.io.IOException;

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
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化
     * @param data
     * @param clz
     * @return
     */
    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;
}
