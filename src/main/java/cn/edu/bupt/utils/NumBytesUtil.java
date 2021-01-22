package cn.edu.bupt.utils;

public class NumBytesUtil {
    public static byte[] getBytesFromShort(short value) {
        return new byte[] {
                (byte) ((value >> 8) & 0xff),
                (byte) (value & 0xff)
        };
    }

    public static byte[] getBytesFromInt(int value) {
        return new byte[] {
                (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    public static short getShortFromTwoBytes(byte high, byte low) {
        return (short) (((high & 0xff) << 8) + (low & 0xff));
    }
}
