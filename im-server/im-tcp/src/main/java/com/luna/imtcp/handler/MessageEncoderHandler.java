package com.luna.imtcp.handler;

import com.luna.common.utils.JsonUtils;
import com.luna.imtcp.api.vo.MessagePack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MessageEncoderHandler extends MessageToMessageEncoder<MessagePack> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePack pack, List<Object> out) throws Exception {
        try {
            String str = JsonUtils.toStr(pack);
            ByteBuf byteBuf = ctx.alloc().buffer(8 + str.length());
            byte[] bytes = str.getBytes();
            byteBuf.writeInt(pack.getCommand());
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
            out.add(new BinaryWebSocketFrame(byteBuf));
        } catch (Exception e) {
            log.error("MessageEncoderHandler 编码异常", e);
        }
    }
}
