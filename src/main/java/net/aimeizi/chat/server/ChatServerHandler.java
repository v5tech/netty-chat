package net.aimeizi.chat.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 服务端消息处理类
 * <p>
 * 这里需要注意以下方法的执行顺序：
 * handlerAdded先于channelActive
 * channelInactive先于handlerRemoved
 *
 * @author fengjing
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 服务器端接收消息处理类
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 从上下文中获取当前Channel
        Channel incoming = ctx.channel();
        // 遍历所有的Channel，发送广播消息
        for (Channel channel : channels) {
            // 向其他客户端发送消息
            if (channel != incoming) {
                channel.writeAndFlush("[" + incoming.remoteAddress() + "] " + msg + "\n");
            } else {
                // 自己
                channel.writeAndFlush("[you]" + msg + "\n");
            }
        }
    }


    /**
     * 优先于channelActive方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        // 在服务器端控制台打印客户端上线信息
        System.out.println("[CLIENT] - " + incoming.remoteAddress() + "上线了...");
        // 给所有在线客户端发送上线广播
        for (Channel channel : channels) {
            channel.writeAndFlush("[系统消息] - " + incoming.remoteAddress() + "上线了\n");
        }
        channels.add(ctx.channel());
    }

    /**
     * 处理客户端上线，在handlerAdded之后调用
     * @param ctx
     * @throws Exception
     */
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        Channel incoming = ctx.channel();
//        // 在服务器端控制台打印客户端上线信息
//        System.out.println("[CLIENT] - " + incoming.remoteAddress() + "上线了...");
//        // 给所有在线客户端发送上线广播
//		for (Channel channel : channels) {
//			channel.writeAndFlush("[系统消息] - " + incoming.remoteAddress() + "上线了\n");
//        }
//    }

    /**
     * 处理客户端下线，在handlerRemoved之前调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        // 在服务器端控制台打印客户端掉线信息
        System.out.println("[CLIENT] - " + incoming.remoteAddress() + "掉线了...");
        // 给所有在线客户端发送掉线广播
        for (Channel channel : channels) {
            channel.writeAndFlush("[系统消息] - " + incoming.remoteAddress() + "掉线了\n");
        }
    }

    /**
     * 在channelInactive方法之后调用
     * @param ctx
     * @throws Exception
     */
//    @Override
//    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//        Channel incoming = ctx.channel();
//        // 在服务器端控制台打印客户端掉线信息
//        System.out.println("[CLIENT] - " + incoming.remoteAddress() + "掉线了...");
//        // 给所有在线客户端发送掉线广播
//        for (Channel channel : channels) {
//            channel.writeAndFlush("[系统消息] - " + incoming.remoteAddress() + "掉线了\n");
//        }
//
//        // A closed Channel is automatically removed from ChannelGroup,
//        // so there is no need to do "channels.remove(ctx.channel());"
//    }


    /**
     * 服务器端异常处理
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}
