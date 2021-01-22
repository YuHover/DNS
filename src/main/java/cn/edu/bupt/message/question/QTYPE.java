package cn.edu.bupt.message.question;

public enum QTYPE {
    A(1), NS(2), MD(3), MF(4), CNAME(5),
    SOA(6), MB(7), MG(8), MR(9), NULL(10),
    WKS(11), PTR(12), HINOF(13), MINFO(14), MX(15),
    TXT(16), AXFR(252), MAILB(253), MAILA(254), ALL(255);

    private final int value;

    QTYPE(int value) {
        this.value = value;
    }

    public static QTYPE ofValue(int value) {
        for (QTYPE qType : QTYPE.values()) {
            if (qType.value == value) {
                return qType;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
