package protocol;

import lombok.Data;

/**
 * @author wanjiahao
 */
@Data
public class MsgHeader {
    /**
     * 魔数
     */
    private short magic;
    /**
     * 请求id
     */
    private long requestId;
    /**
     * 消息类型
     */
    private byte msgType;
    /**
     * 序列化算法
     */
    private byte serialization;
    /**
     * 状态
     */
    private byte status;
    /**
     * 数据长度
     */
    private int msgLen;
}
