package cn.edu.bupt;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class Global {

    public static final int DNS_PORT = 53;
    public static final int BUFFER_LENGTH = 512;
    public static final int HANDLE_THREAD_NUM;
    public static final int SEND_SENIOR_THREAD_NUM;
    public static final int RECEIVE_SENIOR_THREAD_NUM;
    public static final String LOCAL_FILE_NAME;
    public static final String ILLEGAL_IP;
    public static final String SENIOR_DNS_IP;
    public static final SocketAddress SENIOR_DNS_ADDRESS;

    public static final BlockingQueue<DatagramPacket> DNS_REQUESTS = new LinkedBlockingQueue<>();
    public static final BlockingQueue<DatagramPacket> DNS_RESPONSES = new LinkedBlockingQueue<>();
    public static final BlockingQueue<DatagramPacket> INCAPABLE_DNS_REQUESTS = new LinkedBlockingQueue<>();

    static {
        InputStream in = null;
        try {
            in = new FileInputStream("dns.properties");
            Properties props = new Properties();
            props.load(in);

            HANDLE_THREAD_NUM = Integer.parseInt(props.getProperty("HANDLE_THREAD_NUM"));
            SEND_SENIOR_THREAD_NUM = Integer.parseInt(props.getProperty("SEND_SENIOR_THREAD_NUM"));
            RECEIVE_SENIOR_THREAD_NUM = Integer.parseInt(props.getProperty("RECEIVE_SENIOR_THREAD_NUM"));
            LOCAL_FILE_NAME = props.getProperty("LOCAL_FILE_NAME", "dnsrelay.txt");
            ILLEGAL_IP = props.getProperty("ILLEGAL_IP", "0.0.0.0");
            SENIOR_DNS_IP = props.getProperty("SENIOR_DNS_IP");
            SENIOR_DNS_ADDRESS = new InetSocketAddress(SENIOR_DNS_IP, DNS_PORT);
        } catch (IOException e) {
            throw new RuntimeException("Fail to load configuration.");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Logger.getGlobal().info("加载配置文件成功");
    }
}
