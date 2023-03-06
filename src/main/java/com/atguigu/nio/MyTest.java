package com.atguigu.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class MyTest {
    public static void main(String[] args) throws IOException {
        //创建文件的输入流
        File file1 = new File("C:\\Users\\a1382\\Desktop\\NettyPro\\file01.txt");
        FileInputStream fileInputStream = new FileInputStream(file1);
        FileChannel fileInputStreamchannel1 = fileInputStream.getChannel();

        //创建文件的输出流
        File file2 = new File("C:\\Users\\a1382\\Desktop\\NettyPro\\file02.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(file2);
        FileChannel fileOutputStreamchannel2 = fileOutputStream.getChannel();

        //创建缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file1.length());
        fileInputStreamchannel1.read(byteBuffer);

        byteBuffer.flip();

        fileOutputStreamchannel2.write(byteBuffer);

        fileInputStream.close();
        fileOutputStream.close();







    }
}
