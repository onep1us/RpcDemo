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
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化
     * @param data
     * @param clz
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;

    /**
     * 获取code
     * @return
     */
    int getCode();
}
