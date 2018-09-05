package com.klfront.utils;

/**
 * Created by lujl on 2017/6/26.
 * 字节序：大于一个字节的数据在内存中的存放顺序，分为两类： 大端序Big-Endian和小端存模式Little-Endian。
 * 小端序和大端序都是从存储器的低地址开始向高地址存储数据，不同的是大端序低地址存放数据高位，而小端存低地址存放数据低位，。
 * 如：位宽为32bit的CPU，要存储的字数据为0x12345678，起始地址为0x4000。
     大端存储如下（先存高位）：
        地址 	 0x4000 	 0x4001 	 0x4002 	 0x4003
        内容 	 0x12 	    0x34 	    0x56	    0x78
    小端存储如下（先存低位）：
         地址 	 0x4000 	 0x4001 	 0x4002 	 0x4003
         内容 	 0x78 	    0x56	    0x34 	    0x12
 */
public class ByteConverter {
    /**
     * 小端模式
     * int到byte[] 返回低位在前（左），高位在后顺序存储的结果
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytes(int value)
    {
        byte[] desc = new byte[4];
        desc[0] =  (byte) (value & 0xFF);
        desc[1] =  (byte) ((value>>8) & 0xFF);
        desc[2] =  (byte) ((value>>16) & 0xFF);
        desc[3] =  (byte) ((value>>24) & 0xFF);
        return desc;
    }

    /**
     * 小端模式
     * byte[]到int 按低位在前（左），高位在后顺序规则转换
     * @param bytes
     * @param index 默认0
     * @return
     */
    public static int bytesToInt(byte[] bytes, int index)  {
        return (int) (
                (int) (0xff & bytes[index]) |
                        (int) (0xff & bytes[index + 1]) << 8 |
                        (int) (0xff & bytes[index + 2]) << 16 |
                        (int) (0xff & bytes[index + 3]) << 24
        );
    }


    /**
     * 大端模式
     * int到byte[] 返回由高位到低位存储的结果
     * @param i 要转换的int值
     * @return 转换得到的byte数组
     */
    public static byte[] intToBytes2(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF); //右移3个8位，取实际值最左边8位（高8位），存到低地址端
        result[1] = (byte)((i >> 16) & 0xFF); //右移2个8位，取从左开始第二个8位
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);       //和0xFF与运算，取实际值最右边8位（低8位）
        return result;
    }

    /**
     * 大端模式
     * byte[]转int，适用于按高位在前,低位在后的顺序存储的数组
     */
    public static int bytesToInt2(byte[] bytes, int index) {
        int value;
        value = (int) ( ((bytes[index] & 0xFF)<<24)
                |((bytes[index+1] & 0xFF)<<16)
                |((bytes[index+2] & 0xFF)<<8)
                |(bytes[index+3] & 0xFF));
        return value;
    }
}
