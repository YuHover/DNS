package cn.edu.bupt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class Main {
    public static void main( String[] args ) throws SocketException {
        // threads that handle the dns requests
        for (int i = 0; i < Global.HANDLE_THREAD_NUM; i++) {
            new HandleRequestThread().start();
        }

        // threads that send incapable requests to senior dns server
        for (int i = 0; i < Global.SEND_SENIOR_THREAD_NUM; i++) {
            new SendToSeniorThread().start();
        }

        // udp socket that binds ip:0.0.0.0 & port:53
        DatagramSocket socket = new DatagramSocket(Global.DNS_PORT);

        // thread that sends dns responses to clients
        new Thread(() -> {
            while (true) {
                try {
                    socket.send(Global.DNS_RESPONSES.take());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // thread that receives dns requests from clients
        while (true) {
            try {
                DatagramPacket request = new DatagramPacket(new byte[Global.BUFFER_LENGTH], Global.BUFFER_LENGTH);
                socket.receive(request);
                Global.DNS_REQUESTS.put(request);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
