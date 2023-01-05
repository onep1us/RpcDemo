package codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import protocol.MsgHeader;
import protocol.MsgTypeEnum;
import protocol.ProtocolConstants;

import java.util.List;

/**
 * @author wanjiahao
 */
public class RpcDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        short magic = in.readShort();
        if(magic != ProtocolConstants.MAGIC){
            throw new IllegalArgumentException("wrong magic number : " + magic);
        }
        byte msgType = in.readByte();
        byte serialization = in.readByte();
        byte status = in.readByte();
        int msgLen = in.readInt();
        if (in.readableBytes() < msgLen) {
            //没有接收完整
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[msgLen];
        in.readBytes(data);
        MsgTypeEnum msgTypeEnum = MsgTypeEnum.findByType(msgType);
        if(null == msgTypeEnum){
            //todo 写一个异常类
            return;
        }
        MsgHeader header = new MsgHeader();
        header.setSerialization(serialization);
        header.setMsgType(msgType);
    }
}
