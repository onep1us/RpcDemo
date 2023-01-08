package codec;

import enums.SerializeExceptionEnum;
import exception.SerializeException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import protocol.MsgHeader;
import protocol.RpcProtocol;
import serialization.CommonSerialization;
import serialization.SerializationFactory;

/**
 * @author wanjiahao
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol<Object> rpcProtocol, ByteBuf byteBuf) throws Exception {
        MsgHeader header = rpcProtocol.getHeader();
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getMsgType());
        byteBuf.writeByte(header.getSerialization());
        byteBuf.writeByte(header.getStatus());
        CommonSerialization rpcSerialization = SerializationFactory.getSerialization(header.getSerialization());
        byte[] data = rpcSerialization.serialize(rpcProtocol.getBody());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}
