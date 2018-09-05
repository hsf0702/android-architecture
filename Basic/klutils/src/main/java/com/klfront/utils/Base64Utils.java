package com.klfront.utils;


import java.io.UnsupportedEncodingException;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * Created by L on 2016/5/18.
 */

public class Base64Utils
{
    // 加密
    public static String getBase64(String str)
    {
        byte[] b = null;
        String s = null;
        try
        {
            b = str.getBytes("utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        if (b != null)
        {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

    // 解密
    public static String getFromBase64(String s)
    {
        byte[] b = null;
        String result = null;
        if (s != null)
        {
            BASE64Decoder decoder = new BASE64Decoder();
            try
            {
                b = decoder.decodeBuffer(s);
                result = new String(b, "utf-8");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static byte[] getBytesFromBase64(String s)
    {
        byte[] b = null;
        if (s != null)
        {
            BASE64Decoder decoder = new BASE64Decoder();
            try
            {
                b = decoder.decodeBuffer(s);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return  b;
    }
}
