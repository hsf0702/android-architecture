package com.klfront.utils;

/**
 * Created by L on 2015/11/14.
 */


import javax.crypto.*;
import javax.crypto.Cipher;
import javax.crypto.spec.DESedeKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

import java.security.Key;

/**
 * 使用DES加密和解密的方法
 */
public class CryptoUtils
{
    //加密
    public static String encrypt(byte[] key, String data)
    {
        try
        {
            byte[] datas = data.getBytes("UTF-8");
            byte[] str3 = des3EncodeECB(key, datas);
            return new BASE64Encoder().encode(str3);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    //解密
    public static String decrypt(byte[] key, String data)
    {
        try
        {
            byte[] datas = new BASE64Decoder().decodeBuffer(data);
            byte[] str3 = ees3DecodeECB(key, datas);
            return new String(str3, "UTF-8");
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * @param key  密钥
     * @param data 明文
     * @return Base64编码的密文
     * @throws Exception
     */
    private static byte[] des3EncodeECB(byte[] key, byte[] data) throws Exception
    {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, deskey);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }

    /**
     * @param key  密钥
     * @param data Base64编码的密文
     * @return 明文
     * @throws Exception
     */
    private static byte[] ees3DecodeECB(byte[] key, byte[] data) throws Exception
    {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, deskey);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }

}