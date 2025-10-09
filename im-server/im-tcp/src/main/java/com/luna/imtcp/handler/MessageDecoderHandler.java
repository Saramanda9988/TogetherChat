package com.luna.imtcp.handler;

import com.luna.imtcp.api.vo.WebMessage;
import com.luna.imtcp.utils.ByteToMessageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

public class MessageDecoderHandler extends MessageToMessageDecoder<BinaryWebSocketFrame> {
    @Override
    protected void decode(ChannelHandlerContext cxt, BinaryWebSocketFrame msg, List<Object> list) throws Exception {
        ByteBuf content = msg.content();
        WebMessage message = ByteToMessageUtil.decode(content);
        if (message == null) {
            cxt.channel().close();
            return;
        }
        list.add(message);
    }
}
