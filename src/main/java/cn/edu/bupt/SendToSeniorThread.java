package cn.edu.bupt;

import cn.edu.bupt.utils.NumBytesUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class SendToSeniorThread extends Thread {
    private final ConcurrentHashMap<Short, DatagramPacket> pending;
    private final DatagramSocket socket;
    private Short id = 0;

    public SendToSeniorThread() throws SocketException {
        this.pending = new ConcurrentHashMap<>();
        this.socket = new DatagramSocket();
    }

    @Override
    public void run() {
        for (int i = 0; i < Global.RECEIVE_SENIOR_THREAD_NUM; i++) {
            new ReceiveFromSeniorThread(socket, pending).start();
        }

        while (true) {
            try {
                DatagramPacket packet = Global.INCAPABLE_DNS_REQUESTS.take();
                pending.put(id, packet);

                DatagramPacket sendPacket = new DatagramPacket(
                        Arrays.copyOf(packet.getData(), packet.getLength()),
                        packet.getLength(),
                        Global.SENIOR_DNS_ADDRESS
                );
                byte[] bytes = NumBytesUtil.getBytesFromShort(id);
                sendPacket.getData()[0] = bytes[0];
                sendPacket.getData()[1] = bytes[1];
                socket.send(sendPacket);

                id++;
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
