package com.klfront.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by L on 2016/12/6.
 */
public class BitConverter {
    public static byte[] getBytes(boolean x) {
        return new byte[]{
                (byte) (x ? 1 : 0)
        };
    }

    public static byte[] getBytes(char c) {
        return new byte[]{
                (byte) (c & 0xff),//低
                (byte) (c >> 8 & 0xff)};//高
    }

    public static byte[] getBytes(double x) {
        return getBytes(
                Double.doubleToRawLongBits(x));
    }

    public static byte[] getBytes(short x) {
        return new byte[]{
                (byte) x,
                (byte) (x >>> 8)
        };
    }

    /**
     * 返回由低位到高位存储的结果
     *
     * @param x
     * @return
     */
    public static byte[] getBytes(int x) {
        return new byte[]{  // >>>:无符号右移，忽略符号位，空位都以0补齐
                (byte) x,
                (byte) (x >>> 8),
                (byte) (x >>> 16),
                (byte) (x >>> 24),//最高8位，存储在了buffer[3]位置

        };
    }

    public static byte[] getBytes(long x) {
        return new byte[]{
                (byte) (x),
                (byte) (x >>> 8),
                (byte) (x >>> 16),
                (byte) (x >>> 24),
                (byte) (x >>> 32),
                (byte) (x >>> 40),
                (byte) (x >>> 48),
                (byte) (x >>> 56)
        };
    }

    public static byte[] getBytes(float x) {
        return getBytes(
                Float.floatToRawIntBits(x));
    }

    public static byte[] getBytes(String x) {
        return x.getBytes();
    }

    public static long doubleToInt64Bits(double x) {
        return Double.doubleToRawLongBits(x);
    }

    public static double int64BitsToDouble(long x) {
        return (double) x;
    }

    public static boolean toBoolean(byte[] bytes, int index) throws Exception {
        if (bytes.length != 1) {
            throw new Exception("The length of the byte array must be at least 1 byte long.");
        }
        return bytes[index] != 0;
    }

    public char toChar(byte[] bytes, int index) throws Exception {
        if (bytes.length != 2) {
            throw new Exception("The length of the byte array must be at least 2 bytes long.");
        }
        return (char) (
                (0xff & bytes[index]) << 8 |
                        (0xff & bytes[index + 1]) << 0
        );
    }

    public double toDouble(byte[] bytes, int index) throws Exception {
        if (bytes.length != 8) {
            throw new Exception("The length of the byte array must be at least 8 bytes long.");
        }
        return Double.longBitsToDouble(
                toInt64(bytes, index));
    }

    public static short toInt16(byte[] bytes, int index) throws Exception {
        if (bytes.length != 2) {
            throw new Exception("The length of the byte array must be at least 8 bytes long.");
        }
        return (short) (
                (0xff & bytes[index]) |
                        (0xff & bytes[index + 1]) << 8
        );
    }

    public static int toInt32(byte[] bytes, int index) throws Exception {
        if (bytes.length != 4) {
            throw new Exception("The length of the byte array must be at least 4 bytes long.");
        }
        return (int) (
                (int) (0xff & bytes[index]) |
                        (int) (0xff & bytes[index + 1]) << 8 |
                        (int) (0xff & bytes[index + 2]) << 16 |
                        (int) (0xff & bytes[index + 3]) << 24
        );
    }

    public static long toInt64(byte[] bytes, int index) throws Exception {
        if (bytes.length != 8) {
            throw new Exception("The length of the byte array must be at least 8 bytes long.");
        }
        return (long) (
                (long) (0xff & bytes[index]) << 0 |
                        (long) (0xff & bytes[index + 1]) << 8 |
                        (long) (0xff & bytes[index + 2]) << 16 |
                        (long) (0xff & bytes[index + 3]) << 24 |
                        (long) (0xff & bytes[index + 4]) << 32 |
                        (long) (0xff & bytes[index + 5]) << 40 |
                        (long) (0xff & bytes[index + 6]) << 48 |
                        (long) (0xff & bytes[index + 7]) << 56
        );
    }

    public static float toSingle(byte[] bytes, int index) throws Exception {
        if (bytes.length != 4) {
            throw new Exception("The length of the byte array must be at least 4 bytes long.");
        }
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        //return Float.intBitsToFloat(toInt32(bytes, index));
    }

    public static String toString(byte[] bytes) throws Exception {
        if (bytes == null) {
            throw new Exception("The byte array must have at least 1 byte.");
        }
        return new String(bytes);
    }
}
