package com.luna.imtcp.utils;

import com.luna.imtcp.api.vo.MessageHeader;
import com.luna.imtcp.api.vo.WebMessage;
import io.netty.buffer.ByteBuf;

public class ByteToMessageUtil {
    private final static Integer HEADER_LENGTH = 28;

    public static WebMessage decode(ByteBuf in) {

        if (in.readableBytes() < HEADER_LENGTH) {
            return null;
        }
        in.markReaderIndex();

        int command = in.readInt();
        int version = in.readInt();
        int clientType = in.readInt();
        int messageType = in.readInt();
        int appId = in.readInt();
        int imeiLength = in.readInt();
        int bodyLen = in.readInt();

        if (in.readableBytes() < bodyLen + imeiLength) {
            in.resetReaderIndex();
            return null;
        }

        byte[] imeiData = new byte[imeiLength];
        in.readBytes(imeiData);
        String imei = new String(imeiData);

        byte[] bodyData = new byte[bodyLen];
        in.readBytes(bodyData);
        String body = new String(bodyData);

        MessageHeader header = MessageHeader.builder()
                .command(command)
                .version(version)
                .clientType(clientType)
                .messageType(messageType)
                .appId(appId)
                .imeiLength(imeiLength)
                .length(bodyLen)
                .imei(imei)
                .build();

        // 这里没有对body进行反序列化，直接传输字符串，到了后面进行消息传输的时候进行序列化，确定是什么类型的消息
        WebMessage webMessage = new WebMessage();
        webMessage.setMessageHeader(header);
        webMessage.setMessagePack(body);
        return webMessage;
    }
}
