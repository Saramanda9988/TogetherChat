package com.luna.imtcp.handler;

import com.luna.imtcp.api.vo.MessageHeader;
import com.luna.imtcp.api.vo.WebMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MessageEncoderHandler extends MessageToMessageEncoder<WebMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, WebMessage msg, List<Object> out) throws Exception {
        try {
            MessageHeader messageHeader = msg.getMessageHeader();
            String messagePack = msg.getMessagePack();
            
            // 将消息体转换为字节数组
            byte[] bodyData = messagePack.getBytes();
            byte[] imeiData = messageHeader.getImei().getBytes();
            
            // 计算总长度
            int totalLength = 4 + 4 + 4 + 4 + 4 + 4 + 4 + imeiData.length + bodyData.length;
            
            // 分配缓冲区
            ByteBuf byteBuf = ctx.alloc().buffer(totalLength);
            
            // 按照decode方法的顺序编码
            byteBuf.writeInt(messageHeader.getCommand());
            byteBuf.writeInt(messageHeader.getVersion());
            byteBuf.writeInt(messageHeader.getClientType());
            byteBuf.writeInt(messageHeader.getMessageType());
            byteBuf.writeInt(messageHeader.getAppId());
            byteBuf.writeInt(imeiData.length);
            byteBuf.writeInt(bodyData.length);
            byteBuf.writeBytes(imeiData);
            byteBuf.writeBytes(bodyData);
            
            out.add(new BinaryWebSocketFrame(byteBuf));
        } catch (Exception e) {
            log.error("MessageEncoderHandler 编码异常", e);
        }
    }
}