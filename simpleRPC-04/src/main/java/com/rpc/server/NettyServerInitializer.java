package com.rpc.server;

import com.rpc.service.ServiceProvider;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.AllArgsConstructor;

/**
 * @author weiyu_zeng
 *
 * 初始化，主要负责序列化的编码解码， 需要解决netty的粘包问题
 *
 * ChannelInitializer；它提供了一个简单的方法来初始化一个 Channel，一种特殊的ChannelInboundHandler，
 * 用于在某个Channel注册到EventLoop后，对这个Channel执行一些初始化操作。
 * ChannelPipeline：是ChannelHandler的容器，它负责ChannelHandler的管理和事件拦截与调度。
 * 内部维护了一个ChannelHandler的链表和迭代器，可以方便地实现ChannelHandler查找、添加、替换和删除
 *
 * Netty的消息传递都是基于流，通过Channel和Buffer传递的，自然，Object也需要转换成Channel和Buffer来传递
 * Netty本身已经给我们写好了这样的转换工具。ObjectEncoder和ObjectDecoder：
 * Netty给我们处理自己业务的空间是在灵活的可子定义的Handler上的，也就是说，如果我们自己去做这个转换工作，
 * 那么也应该在Handler里去做。而Netty，提供给我们的ObjectEncoder和Decoder也恰恰是一组Handler
 *
 * LengthFieldBasedFrameDecoder：自定义长度帧解码器，解码器自定义长度解决TCP粘包黏包问题。（
 *  TCP粘包是指发送方发送的若干个数据包到接收方时粘成一个包。从接收缓冲区来看，后一个包数据的头紧接着前一个数据的尾
 *  当TCP连接建立后，Client发送多个报文给Server，TCP协议保证数据可靠性，但无法保证Client发了n个包，服务端也按照n个包接收。
 *  Client端发送n个数据包，Server端可能收到n-1或n+1个包。
 *  ）
 *  如何解决粘包现象
 * 1. 添加特殊符号，接收方通过这个特殊符号将接收到的数据包拆分开 - DelimiterBasedFrameDecoder特殊分隔符解码器
 * 2. 每次发送固定长度的数据包 - FixedLengthFrameDecoder定长编码器
 * 3. 在消息头中定义长度字段，来标识消息的总长度 - LengthFieldBasedFrameDecoder自定义长度解码器 √
 */
@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceProvider;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 解码器：消息格式 [长度][消息体]，解决粘包问题
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4,
                        0, 4));
        // 编码器：计算当前待大宋消息的长度，写入到前4个字节中
        pipeline.addLast(new LengthFieldPrepender(4));

        // 这里使用的还是java 序列化方式， netty的自带的解码编码支持传输这种结构
        pipeline.addLast(new ObjectEncoder());
        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
            @Override
            public Class<?> resolve(String className) throws ClassNotFoundException {
                return Class.forName(className);
            }
        }));

        pipeline.addLast(new NettyRPCServerHandler(serviceProvider));
    }
}
