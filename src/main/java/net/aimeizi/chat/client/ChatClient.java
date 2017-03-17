package net.aimeizi.chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 客户端
 *
 * @author fengjing
 */
public class ChatClient {

    private String host;
    private int port;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        new ChatClient("127.0.0.1", 8079).run();
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatClientInitializer());
            Channel channel = bootstrap.connect(host, port).sync().channel();
            // 从控制台读取用户输入
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                // 读取一行
                String line = in.readLine();
                // 遇到“bye”退出
                if ("bye".equalsIgnoreCase(line)) {
                    channel.close();
                    break;
                } else {
                    // 将用户在控制台中的输入写出
                    channel.writeAndFlush(line + "\r\n");
                }
            }
        } finally {
            group.shutdownGracefully();
        }
    }

}
