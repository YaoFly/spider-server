package crawl;

/**
 * Created by yaofly on 2017/3/8.
 */

import io.netty.buffer.ByteBuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Handles a server-side channel.
 */
public class SpiderServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    private static Logger logger = LoggerFactory.getLogger(SpiderServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject httpObject) throws Exception {
        FullHttpRequest req = (FullHttpRequest) httpObject;
        if (req.method() == POST) { // 处理POST请求
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(
                    new DefaultHttpDataFactory(false), req, Charset.forName("utf-8"));
            InterfaceHttpData strData = decoder.getBodyHttpData("str"); // //
            InterfaceHttpData urlData = decoder.getBodyHttpData("url"); // //
            // 读取从客户端传过来的参数
            String str = "", url = "";
            if (strData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                Attribute attribute = (Attribute) strData;
                str = attribute.getValue();
            }
            if (urlData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                Attribute attribute = (Attribute) urlData;
                url = attribute.getValue();
            }

            //分发请求
            if(req.uri().compareToIgnoreCase("/html")==0
                    && req.method()== HttpMethod.POST) {
                new Spider().HandleStringByHtml(str, url);
            }
            if(req.uri().compareToIgnoreCase("/json")==0
                    && req.method()== HttpMethod.POST) {
                new Spider().HandleStringByJson(str, url);
            }
        }
        ctx.writeAndFlush(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
