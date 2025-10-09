package com.luna.imtcp.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.luna.imtcp.api.vo.MessageHeader;
import com.luna.imtcp.api.vo.WebMessage;
import com.luna.imtcp.command.CommandProcessor;
import com.luna.imtcp.utils.UserChannelRepository;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@ChannelHandler.Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<WebMessage> {

    @Value("${imtcpserver.config.broker-id}")
    private Integer brokerId;

    private CommandProcessor commandProcessor;

    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");

    // 当web客户端连接后，触发该方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.commandProcessor = SpringUtil.getBean(CommandProcessor.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebMessage webMessage) throws Exception {
        MessageHeader messageHeader = webMessage.getMessageHeader();
        Integer command = messageHeader.getCommand();
        log.info("Received WebSocket message: command={}, brokerId={}", command, brokerId);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UserChannelRepository.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        UserChannelRepository.remove(ctx.channel());
    }

    /**
     * 取消绑定
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 可能出现业务判断离线后再次触发 channelInactive
        log.warn("触发 channelInactive 掉线![{}]", ctx.channel().id());
        UserChannelRepository.remove(ctx.channel());
    }

    /**
     * 心跳检查
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent idleStateEvent) {
            // 读空闲
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // 关闭用户的连接
                UserChannelRepository.remove(ctx.channel());
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            String token = ctx.channel().attr(TOKEN).get();
            if (StrUtil.isNotBlank(token)) {
                UserChannelRepository.bind();
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
