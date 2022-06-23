package com.anacmer.netty.server.bio;


import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BID Server Demo
 * 1.运行 socket server
 * 2.通过 telnet host port 连接
 * 3.ctrl + ] 进入到 telnet 客户端
 * 4.使用 send message 指令向 server 发送消息
 *
 * @Author ZhaoJQ
 * @Date 2022/6/23 23:03
 */
public class BIOServerDemo {

    public static void main(String[] args) throws IOException {

//        1.创建一个线程池
//        2.如果有客户连接，就创建一个线程，与之通讯
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 创建 server socket
        try (ServerSocket serverSocket = new ServerSocket(6666)) {
            System.out.println("服务器启动了...");

            while (true) {
                Socket accept = serverSocket.accept();
                System.out.println("有客户端来访问了");
                executorService.execute(() -> {
                    handler(accept);
                });

            }
        }
    }

    public static void handler(Socket socket) {
        byte[] bytes = new byte[1024];
        try {
            System.out.println("线程信息 id=" + Thread.currentThread().getId());
            InputStream inputStream = socket.getInputStream();
            while (true) {
                int read = inputStream.read(bytes);
                if (read != -1) {
                    System.out.println(new String(bytes, 0, read));
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
