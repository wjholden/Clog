package com.wjholden;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Collection;

public class Syslog implements Runnable {
    private final int port;
    private final Collection<String> queue;

    public Syslog(int port, Collection<String> queue) {
        this.port = port;
        this.queue = queue;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(514)) {
            byte[] buf = new byte[1500];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            while (true) {
                socket.receive(packet);
                String s = new String(buf, 5, packet.getLength() - 5);
                synchronized (queue) {
                    queue.add(s);
                }
                queue.notify();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
