package cn.edu.bupt.message.question;

public class Question {
    private final byte[] data;
    private final String domainName;
    private final QTYPE qType;
    private final QCLASS qClass;

    public static final int QTYPE_LENGTH = 2;
    public static final int QCLASS_LENGTH = 2;

    public Question(byte[] data) {
        this.data = data;

        this.qType = obtainQType();
        this.qClass = obtainQClass();
        this.domainName = obtainDomainName();
    }

    public String getDomainName() {
        return domainName;
    }

    public QTYPE getQType() {
        return qType;
    }

    public QCLASS getQClass() {
        return qClass;
    }

    private String obtainDomainName() {
        StringBuilder sb = new StringBuilder();
        int end = this.data.length - QTYPE_LENGTH - QCLASS_LENGTH - 1;
        int follow = 0;
        for (int i = 0; i < end; i += (follow + 1)) {
            follow = this.data[i];
            for (int j = i + 1; j <= i + follow; j++) {
                sb.append((char) this.data[j]);
            }

            sb.append('.');
        }

        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private QTYPE obtainQType() {
        int position = this.data.length - QCLASS_LENGTH - QTYPE_LENGTH;
        return QTYPE.ofValue(getIntFromTwoBytes(data[position], data[position + 1]));
    }

    private QCLASS obtainQClass() {
        int position = this.data.length - QCLASS_LENGTH;
        return QCLASS.ofValue(getIntFromTwoBytes(data[position], data[position + 1]));
    }

    private int getIntFromTwoBytes(byte high, byte low) {
        return ((high & 0xff) << 8) + (low & 0xff);
    }
}
