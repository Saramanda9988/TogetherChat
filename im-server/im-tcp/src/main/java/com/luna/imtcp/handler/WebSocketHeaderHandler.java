package com.luna.imtcp.handler;

import cn.hutool.core.net.url.UrlBuilder;
import com.luna.imtcp.utils.NettyUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;

/**
 * WebSocket Token提取处理器
 * 在WebSocket握手前提取URL中的token参数
 */
@Slf4j
public class WebSocketHeaderHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest request) {
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());

            log.info("request uri: " + request.uri() + " ws开始链接");

            // 获取token参数
            String token = Optional
                    .ofNullable(urlBuilder.getQuery())
                    .map(k->k.get("token")).
                    map(CharSequence::toString)
                    .orElse("");

            NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, token);

            // 获取请求路径
            request.setUri(urlBuilder.getPath().toString());
            HttpHeaders headers = request.headers();

            //经过nginx的地址，
            String ip = headers.get("X-Real-IP");
            // 如果没有经过nginx，就直接获取远端地址
            if (StringUtils.isEmpty(ip)) {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = address.getAddress().getHostAddress();
            }
            NettyUtil.setAttr(ctx.channel(), NettyUtil.IP, ip);

            ctx.pipeline().remove(this);
            ctx.fireChannelRead(request);
        }
        
        // 继续传递消息
        super.channelRead(ctx, msg);
    }
}