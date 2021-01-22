package cn.edu.bupt;

import cn.edu.bupt.message.DNSRequest;
import cn.edu.bupt.message.OPCODE;
import cn.edu.bupt.message.question.QCLASS;
import cn.edu.bupt.message.question.QTYPE;
import cn.edu.bupt.message.question.Question;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class HandleRequestThread extends Thread {

    private static final HashMap<String, String> localRecords = new HashMap<>();
    static {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(new FileInputStream(Global.LOCAL_FILE_NAME), StandardCharsets.UTF_8)
            );

            String line;
            while ((line = in.readLine()) != null) {
                String[] domainIP = line.strip().split(" ");
                localRecords.put(domainIP[1], domainIP[0]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Logger.getGlobal().info("加载本地非法域名文件 " + Global.LOCAL_FILE_NAME + " 成功");
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 1. 根据DNS request数据构建DNSRequest对象
                DatagramPacket request = Global.DNS_REQUESTS.take();
                DNSRequest requestObj = new DNSRequest(request.getData(), request.getLength());

                // 2. 根据已有信息判断是否交给上层处理
                boolean truncated = requestObj.isTruncated();
                OPCODE opcode = requestObj.getOpcode();
                int questionCount = requestObj.getQuestionCount();
                List<Question> questions = requestObj.getQuestions();
                if (truncated || !OPCODE.QUERY.equals(opcode) || questionCount != 1) {
                    senior(request); // 交给上层处理
                    continue;
                }

                Question onlyOne = questions.get(0);
                String domainName = onlyOne.getDomainName();

                Logger.getGlobal().info(Thread.currentThread().getName() + " ====> 查询 " + domainName);

                String ip = localRecords.get(domainName); // 本地搜寻
                if (ip == null) {
                    senior(request); // 本地无此域名信息 交给上层处理
                    continue;
                }

                if (Global.ILLEGAL_IP.equals(ip)) { // 非法域名
                    requestObj.flipToResponse();
                    Logger.getGlobal().severe(Thread.currentThread().getName() + " ====> 非法域名 " + domainName);
                    requestObj.setLegality(false);
                }
                else { // 正常域名
                    QTYPE qType = onlyOne.getQType();
                    QCLASS qClass = onlyOne.getQClass();
                    if (!QTYPE.A.equals(qType) || !QCLASS.IN.equals(qClass)) {
                        senior(request); // 交给上层处理
                        continue;
                    }

                    // 3. 可以处理, 根据本地信息由DNS request构造DNS response
                    requestObj.flipToResponse();
                    requestObj.setLegality(true); // legal
                    requestObj.setAnswerCount((short) 1);
                    requestObj.setPointer((short) 0xc00c); // pointer
                    requestObj.setAnswerQTYPE(qType);
                    requestObj.setAnswerQCLASS(qClass);
                    requestObj.setTTL(86400); // TTL
                    requestObj.setRDLength((short) 4);
                    requestObj.setRDATA(InetAddress.getByName(ip).getAddress()); // IP
                }

                Global.DNS_RESPONSES.put(new DatagramPacket(
                        requestObj.getData(),
                        requestObj.getLength(),
                        request.getSocketAddress())
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void senior(DatagramPacket packet) {
        try {
            Global.INCAPABLE_DNS_REQUESTS.put(packet);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
