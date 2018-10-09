package com.nio.blocking;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * SocketHandler
 */
public class SocketHandler implements Runnable {

    private SocketChannel socketChannel;

    public SocketHandler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            int num;
            while ((num = socketChannel.read(buffer)) > 0) {
                
                buffer.flip();

                byte[] bytes = new byte[num];
                buffer.get(bytes);

                String re = new String(bytes, "UTF-8");
                System.out.println("accept the request " + re);

                ByteBuffer writeBuffer = ByteBuffer.wrap(("there are your request, response you : " + re).getBytes());
                socketChannel.write(writeBuffer);

                buffer.flip();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}