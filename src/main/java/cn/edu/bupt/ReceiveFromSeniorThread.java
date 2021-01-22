package cn.edu.bupt;

import cn.edu.bupt.utils.NumBytesUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentHashMap;

public class ReceiveFromSeniorThread extends Thread {
    private final DatagramSocket socket;
    private final ConcurrentHashMap<Short, DatagramPacket> pending;

    public ReceiveFromSeniorThread(DatagramSocket socket, ConcurrentHashMap<Short, DatagramPacket> pending) {
        this.socket = socket;
        this.pending = pending;
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket response = new DatagramPacket(new byte[Global.BUFFER_LENGTH], Global.BUFFER_LENGTH);
            try {
                socket.receive(response);
                byte[] data = response.getData();
                short id = NumBytesUtil.getShortFromTwoBytes(data[0], data[1]);
                DatagramPacket request = pending.remove(id);

                if (request != null) {
                    data[0] = request.getData()[0];
                    data[1] = request.getData()[1];
                    response.setSocketAddress(request.getSocketAddress());
                    Global.DNS_RESPONSES.put(response);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
