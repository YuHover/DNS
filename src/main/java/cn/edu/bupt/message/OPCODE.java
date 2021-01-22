package cn.edu.bupt.message;

public enum OPCODE {
    QUERY(0), INVERSE_QUERY(1), STATUS(2), RESERVED(3);

    private final int value;

    OPCODE(int value) {
        this.value = value;
    }

    static OPCODE ofValue(int val) {
        if (val < 0 || val > 15) {
            return null;
        }

        for (OPCODE opcode : OPCODE.values()) {
            if (opcode.value == val) {
                return opcode;
            }
        }
        return RESERVED;
    }
}
