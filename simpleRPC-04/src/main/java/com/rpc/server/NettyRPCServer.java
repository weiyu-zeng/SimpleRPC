package com.rpc.server;

import com.rpc.service.ServiceProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;


/**
 * @author weiyu_zeng
 *
 * 实现RPCServer接口，负责监听与发送数据
 *
 * NioEventLoopGroup：实际上就是一个线程池，里面有可执行的Executor#Runnable,同时继承了Iterable 迭代器
 * 每一个 NioEventLoopGroup 中都包含了多个NioEventLoop，而每个 NioEventLoop 又绑定着一个线程。
 * 一个 NioEventLoop 可以处理多个 Channel 中的 IO 操作，而其只有一个线程。所以对于这个线程资源的使用，
 * 就存在了竞争。此时为每一个 NioEventLoop都绑定了一个多跑复用器 Selector，由 Selector 来决定当前 NioEventLoop
 * 的线程为哪些 Channel 服务。
 *
 * ServerBootstrap：负责初始化netty服务器，并且开始监听端口的socket请求。
 * ServerBootstrap用一个ServerSocketChannelFactory 来实例化。ServerSocketChannelFactory 有两种选择，
 * 一种是NioServerSocketChannelFactory，一种是OioServerSocketChannelFactory。前者使用NIO，后则使用普通的阻塞式IO。
 * 它们都需要两个线程池实例作为参数来初始化，一个是boss线程池，一个是worker线程池。
 *
 * ServerBootstrap.bind(int)：负责绑定端口，当这个方法执行后，ServerBootstrap就可以接受指定端口上的socket连接了。
 * 一个ServerBootstrap可以绑定多个端口。bind方法会创建一个serverchannel，并且会将当前的channel注册到eventloop上面.
 *
 * ChannelFuture：最顶层是继承的jdk 的Future 接口，Future 类就是代表了异步计算的结果。
 * Netty 里面的IO操作全部是异步的。这意味着，IO操作会立即返回，但是在调用结束时，无法保证IO操作已完成。取而代之，
 * 将会返回给你一个ChannelFuture 实例，提供IO操作的结果信息或状态。
 * channelFuture.channel()：返回ChannelFuture关联的Channel；
 * channelFuture.channel().closeFuture().sync()相当于在这里阻塞，直到serverchannel关闭。
 *
 * shutdownGracefully()：所有现有channel将自动关闭,并且应拒绝重新连接尝试
 */
@AllArgsConstructor
public class NettyRPCServer implements RPCServer {
    private ServiceProvider serviceProvider;

    @Override
    public void start(int port) {
        // netty服务线程组负责建立连接(TCP/IP连接)，work负责具体的请求
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        System.out.println("Netty服务端启动了");

        try {
            // 启动Netty服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 初始化
            serverBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                           .childHandler(new NettyServerInitializer(serviceProvider));
            // 同步阻塞
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            // 死循环监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {

    }
}
