package codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import model.RpcRequest;
import model.RpcResponse;
import protocol.MsgHeader;
import protocol.MsgTypeEnum;
import protocol.ProtocolConstants;
import protocol.RpcProtocol;
import serialization.CommonSerialization;
import serialization.SerializationFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author wanjiahao
 */
@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        short magic = in.readShort();
        if(magic != ProtocolConstants.MAGIC){
            throw new IllegalArgumentException("wrong magic number : " + magic);
        }
        byte msgType = in.readByte();
        byte serializationType = in.readByte();
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
        header.setMagic(magic);
        header.setSerialization(serializationType);
        header.setMsgType(msgType);
        header.setStatus(status);
        CommonSerialization serialization = SerializationFactory.getSerialization(serializationType);
        if(null == serialization){
            //todo 写一个异常类，未找到序列化器
            return;
        }
        switch (msgTypeEnum){
            case REQUEST:

                RpcRequest rpcRequest = serialization.deserialize(data, RpcRequest.class);
                log.info("rpcRequest json: {}", rpcRequest);
                if(null != rpcRequest){
                    RpcProtocol<RpcRequest> rpcProtocol = new RpcProtocol<>();
                    rpcProtocol.setHeader(header);
                    rpcProtocol.setBody(rpcRequest);
                    out.add(rpcProtocol);
                }
                break;
            case RESPONSE:
                RpcResponse rpcResponse = serialization.deserialize(data, RpcResponse.class);
                if(null != rpcResponse){
                    RpcProtocol<RpcResponse> rpcProtocol = new RpcProtocol<>();
                    rpcProtocol.setHeader(header);
                    rpcProtocol.setBody(rpcResponse);
                    out.add(rpcProtocol);
                }
                break;
            case HEARTBEAT:
                break;
            default:
                return;
        }
    }
}
