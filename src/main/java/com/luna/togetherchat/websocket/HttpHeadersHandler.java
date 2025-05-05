package com.luna.togetherchat.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import com.luna.togetherchat.websocket.util.NettyUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Optional;

@Slf4j
public class HttpHeadersHandler extends ChannelInboundHandlerAdapter {

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
        }else {
            ctx.fireChannelRead(msg);
        }
    }
}