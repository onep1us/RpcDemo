package serialization;

/**
 * 获取序列化器的简单工厂
 * @author wanjiahao
 */
public class SerializationFactory {
    public static CommonSerialization getSerialization(byte serializationType){
        SerializationTypeEnum serializationTypeEnum = SerializationTypeEnum.findByType(serializationType);
        switch (serializationTypeEnum){
            case JSON:
                return new JsonSerialization();
            case KRYO:
                return null;
            default:
                //todo 抛出异常
                return null;
        }
    }
}
