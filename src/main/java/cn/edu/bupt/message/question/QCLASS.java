package cn.edu.bupt.message.question;

public enum QCLASS {
    IN(1), CS(2), CH(3), HS(4);

    private final int value;

    QCLASS(int value) {
        this.value = value;
    }

    public static QCLASS ofValue(int value) {
        for (QCLASS qClass : QCLASS.values()) {
            if (qClass.value == value) {
                return qClass;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
