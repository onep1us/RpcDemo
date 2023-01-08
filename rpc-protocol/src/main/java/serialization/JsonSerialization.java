package serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import model.RpcRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author wanjiahao
 */
@Slf4j
public class JsonSerialization implements CommonSerialization{

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        try{
            byte[] bytes = obj instanceof String ? ((String) obj).getBytes() : MAPPER.writeValueAsString(obj).getBytes(StandardCharsets.UTF_8);
            return bytes;
        }catch (Exception e){
            log.error("JsonSerialization serialize error, obj :{}",obj,e);
            return null;
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        try {
            T obj = MAPPER.readValue(new String(data), clz);
            if (obj instanceof RpcRequest) {
                obj = (T) handleRequest(obj);
            }
            return obj;
        }catch (Exception e){
            log.error("JsonSerialization deserialize error",e);
            return null;
        }
    }

    private RpcRequest handleRequest(Object obj) throws IOException {
        RpcRequest rpcRequest = (RpcRequest) obj;
        for (int i = 0; i < rpcRequest.getParaClass().length; i++) {
            Class<?> clazz = rpcRequest.getParaClass()[i];
            if (!clazz.isAssignableFrom(rpcRequest.getPara()[i].getClass())) {
                log.info("rpcRequest.getPara()[i].getClass() : {}",rpcRequest.getPara()[i].getClass());
                byte[] bytes = MAPPER.writeValueAsBytes(rpcRequest.getPara()[i]);
                rpcRequest.getPara()[i] = MAPPER.readValue(bytes, clazz);
            }
        }
        return rpcRequest;
    }

    @Override
    public int getCode() {
        return SerializationTypeEnum.JSON.getType();
    }
}
