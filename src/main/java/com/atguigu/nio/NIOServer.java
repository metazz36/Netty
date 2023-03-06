package com.atguigu.nio;

import io.netty.channel.ServerChannel;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) throws Exception{
        //创建ServerSocketChannel -> ServerSocket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //得到一个Selecor对象
        Selector selector = Selector.open();
        //绑定一个端口6666, 在服务器端监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6669));
        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        //把 serverSocketChannel 注册到  selector 关心 事件为 OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("注册后的selectionkey 数量=" + selector.keys().size()); // 1

        //循环等待客户端连接
        while (true) {
            //这里我们等待1秒，如果没有事件发生, 返回
            if(selector.select(1000) == 0) { //没有事件发生
                System.out.println("服务器等待了1秒，无连接");
                System.out.println("selector.selectedKeys().size() = " + selector.selectedKeys().size());
                System.out.println("selector.keys().size() =" + selector.keys().size());
                continue;
            }
            System.out.println("监听到连接，selector.select() = " + selector.select());
            //如果返回的>0, 就获取到相关的 selectionKey集合
            //1.如果返回的>0， 表示已经获取到关注的事件
            //2. selector.selectedKeys() 返回关注事件的集合
            //   通过 selectionKeys 反向获取通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            System.out.println("selector.selectedKeys().size() = " + selectionKeys.size());
            System.out.println("selectionKeys.hashCode() =" + selectionKeys.hashCode());

            //遍历 Set<SelectionKey>, 使用迭代器遍历
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while (keyIterator.hasNext()) {
                //获取到SelectionKey
                SelectionKey key = keyIterator.next();
                //根据key 对应的通道发生的事件做相应处理
                if(key.isAcceptable()) { //如果是 OP_ACCEPT, 有新的客户端连接
                    //该客户端生成一个 SocketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();//！！！！阻塞方法，但因为前面判断过已经有连接请求，所以请求会直接响应，体现的就是非阻塞
                    System.out.println("客户端连接成功 生成了一个 socketChannel " + socketChannel.hashCode());
                    System.out.println(socketChannel.getLocalAddress() + " " + socketChannel.getRemoteAddress());
                    //将  SocketChannel 设置为非阻塞
                    socketChannel.configureBlocking(false);
                    //将socketChannel 注册到selector, 关注事件为 OP_READ， 同时给socketChannel
                    //关联一个Buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    System.out.println("客户端连接后 ，注册的selectionkey 数量=" + selector.keys().size()); //2,3,4..
                    System.out.println("客户端连接后 ，selector.selectedKeys().size() =" + selector.selectedKeys().size());
                    System.out.println("selector.selectedKeys().hashCode() =" + selector.selectedKeys().hashCode());
                }
                if(key.isReadable()) {  //发生 OP_READ
                    //通过key 反向获取到对应channel
                    SocketChannel channel = (SocketChannel)key.channel();
                    //获取到该channel关联的buffer
                    ByteBuffer buffer = (ByteBuffer)key.attachment();
                    channel.read(buffer);
                    System.out.println("form 客户端 " + new String(buffer.array()));
                }
                //手动从集合中移除当前的selectionKey, 防止重复操作
                keyIterator.remove();
                System.out.println("移除后 selector.selectedKeys().size() = " + selectionKeys.size());
            }
        }

    }
}
