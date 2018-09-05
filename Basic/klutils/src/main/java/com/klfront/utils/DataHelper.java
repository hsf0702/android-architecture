package com.klfront.utils;


import android.support.annotation.ColorInt;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * Created by L on 2016/12/6.
 */

public class DataHelper {

    public static class QhNoteSteamReadException extends Exception {
        public QhNoteSteamReadException() {
            super("Read from stream error, might EOF.");
        }
    }

    public static boolean IsEqual(byte[] self, byte[] other) {
        return IsEqual(self, 0, self.length, other);
    }

    public static boolean IsEqual(byte[] self, int offset, int count, byte[] other) {
        if (count == other.length && self.length >= offset + count) {
            for (int i = offset; i < offset + count; i++) {
                if (self[i] != other[i]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 读取较长的数组 由于每次读取的长度有限，因此需要循环读取
     * @param stream
     * @param length
     * @return
     * @throws Exception
     */
    public static byte[] ReadLongByteArray(ZipInputStream stream, int length) throws Exception {
        byte[] data = new byte[length];
        int offset =0;
        while (offset<length) {
            int count =512;
            if(length-offset<count)
            {
                count = length-offset;
            }
            int size = stream.read(data, offset, count);
            if(size==-1)
            {
                break;
            }
            offset += size;
        }
        if (offset == data.length) {
            return data;
        }
        throw new QhNoteSteamReadException();
    }
    public static byte[] ReadByteArray(InputStream stream, int length) throws Exception {
        byte[] data = new byte[length];
        int size = 0;// 已经成功读取的字节的个数
        //int size = stream.read(data, 0, length);//读取最大长度length的数据，不保证一定读取到length长度，可能要连续多次才能读取完整。
        while(size<length)
        {
            int temp = stream.read(data, size, length - size);
            if(temp==-1)
            {
                break;
            }
            size += temp;
        }

        if (size == data.length) {
            return data;
        }
        throw new QhNoteSteamReadException();
    }

    public static String ReadString(InputStream stream) throws Exception {
        int size = ReadInt(stream);
        if(size > 0) {
            byte[] data = new byte[size];
            int count = 0;
            while (count < size) {
                int readSize = stream.read(data, count, size-count);
                if (readSize == -1) {
                    break;
                }
                count += readSize;
            }
            if (size == count) {
                return new String(data);
            }
        }
        else
        {
            return null;
        }
        throw new QhNoteSteamReadException();
    }

    public static boolean ReadBoolean(InputStream stream) throws Exception {
        byte[] data = new byte[1];
        int size = 0;
        while (size < data.length) {
            int readSize = stream.read(data, size, data.length - size);
            if (readSize == -1) {
                break;
            }
            size += readSize;
        }
        if (size == data.length) {
            return BitConverter.toBoolean(data, 0);
        }
        throw new QhNoteSteamReadException();
    }

    @ColorInt
    public static int ReadColor(InputStream stream) throws Exception {
        int argb = ReadInt(stream);
        return argb;
    }

    public static long ReadLong(InputStream stream) throws Exception{
        byte[] data = new byte[Long.SIZE/8];
        int size = 0;
        while (size < data.length) {
            int readSize = stream.read(data, size, data.length - size);
            if (readSize == -1) {
                break;
            }
            size += readSize;
        }
        if (size == data.length) {
            return BitConverter.toInt64(data, 0);
        }
        throw new QhNoteSteamReadException();
    }

    public static long ReadDateTime(InputStream stream) throws Exception{
        long timevalue = ReadLong(stream);
        return timevalue;
    }

    public static short ReadShort(InputStream stream) throws Exception {
        byte[] data = new byte[Short.SIZE/8];
        int size = 0;
        while (size < data.length) {
            int readSize = stream.read(data, size, data.length - size);
            if (readSize == -1) {
                break;
            }
            size += readSize;
        }
        if (size == data.length) {
            return BitConverter.toInt16(data, 0);
        }
        throw new QhNoteSteamReadException();
    }

    public static float ReadFloat(InputStream stream) throws Exception {
        byte[] data = new byte[Float.SIZE/8];
        int size = 0;
        while(size<data.length){
            int readSize = stream.read(data, size, data.length - size);
            if(readSize==-1){
                break;
            }
            size += readSize;
        }
        if (size == data.length) {
            return BitConverter.toSingle(data, 0);
        }
        throw new QhNoteSteamReadException();
    }

    public static int ReadInt(InputStream stream) throws Exception{
        byte[] data = new byte[Integer.SIZE/8];
        int size = 0;
        while (size < data.length) {
            int readSize = stream.read(data, size, data.length - size);
            if (readSize == -1) {
                break;
            }
            size += readSize;
        }
        if (size == data.length) {
            return BitConverter.toInt32(data, 0);
        }
        throw new QhNoteSteamReadException();
    }


    public static void AddString(ByteArrayOutputStream stream, String text) {
        if(!TextUtils.isEmpty(text)) {
            byte[] bytes = text.getBytes();
            AddInt(stream, bytes.length);
            stream.write(bytes, 0, bytes.length);
        }
        else
        {
            AddInt(stream, 0);
        }
    }

    public static void AddInt(ByteArrayOutputStream stream, int data) {
        byte[] bytes =BitConverter.getBytes(data);
        stream.write(bytes,0,bytes.length);;
    }

    public static void AddShort(ByteArrayOutputStream stream, short data) {
        byte[] bytes =BitConverter.getBytes(data);
        stream.write(bytes,0,bytes.length);;
    }

    public static void AddFloat(ByteArrayOutputStream stream, float data) {
        byte[] bytes =BitConverter.getBytes(data);
        stream.write(bytes,0,bytes.length);;
    }

    public static void AddLong(ByteArrayOutputStream stream, long data) {
        byte[] bytes =BitConverter.getBytes(data);
        stream.write(bytes,0,bytes.length);
    }

    public static void AddByteArray(ByteArrayOutputStream stream, byte[] data) {
        stream.write(data,0,data.length);
    }

    public static void AddBoolean(ByteArrayOutputStream stream, boolean data) {
        byte[] bytes =BitConverter.getBytes(data);
        stream.write(bytes,0,bytes.length);
    }

}
