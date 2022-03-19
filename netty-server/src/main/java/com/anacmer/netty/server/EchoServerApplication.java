package com.anacmer.netty.server;

import com.anacmer.netty.server.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Netty 服务端
 *
 * @Author ZhaoJQ
 * @Date 2022/3/19 23:25
 */

public class EchoServerApplication {
    private final int port;

    public EchoServerApplication(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        new EchoServerApplication(9999).start();//启动服务
    }

    public void start() throws InterruptedException {
        EchoServerHandler echoServerHandler = new EchoServerHandler();//自定义的Handler
        EventLoopGroup group = new NioEventLoopGroup();//创建EventLoopGroup 处理Channel的所有IO事件
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();//创建ServerBoostrap
            bootstrap.group(group)//指定EventGroup
                    .channel(NioServerSocketChannel.class)//指定所有的NIO传输Channel
                    .localAddress(new InetSocketAddress("localhost", port))//指定服务监听的套接字
                    .childHandler(new ChannelInitializer<SocketChannel>() {//添加一个Handler到Channel的ChannelPipeline（责任链模式）
                        //通过ChannelInitializer
                        //将自定义的Handler处理成Shareable(所有客户端链接都会使用同一个Handler，单例)
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(echoServerHandler);
                        }
                    });
            System.out.println("Netty server loading finished");
            ChannelFuture channelFuture = bootstrap.bind().sync();//异步的绑定服务器，调用sync()方法阻塞等知道绑定完成
            channelFuture.channel().closeFuture().sync();//获取Channel的CloseFuture，并且阻塞当前线程直到完成
        } finally {
            group.shutdownGracefully().sync();//关闭EventLoopGroup，释放所有资源
        }
    }
}
