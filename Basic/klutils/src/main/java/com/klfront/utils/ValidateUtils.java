package com.klfront.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by L on 2016/1/15.
 */
public class ValidateUtils {

    public static boolean containsIP(String text) {
        Pattern pattern = Pattern.compile("(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)");

        Matcher matcher = pattern.matcher(text);
//        return matcher.matches();//match是完全匹配  find是查找
        return matcher.find();
    }

    public static boolean containsUrl(String text)
    {
        Pattern pattern = Pattern.compile("((https|http|ftp|rtsp|mms)?:\\/\\/)[^\\s]+");
        Matcher matcher = pattern.matcher(text);
        //return matcher.matches();
        return matcher.find();
    }

    /**
     * 两个以上的 字符串加.的组合
     * @param text
     * @return
     */
    public static boolean containsDomain(String text)
    {
        Pattern pattern = Pattern.compile("([^\\s]+\\.){2,}");
        Matcher matcher = pattern.matcher(text);
//        return matcher.matches();
        return matcher.find();
    }


    public static boolean isWordPlusNumber(String str) {
        String strPattern = "^[a-zA-Z][a-zA-Z0-9-_]{1,20}$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 字母和数字组合
     *
     * @param str
     * @return
     */
    public static boolean isLetterPlusNumber(String str) {
        String strPattern = "^[a-zA-Z][a-zA-Z0-9-_]{1,20}$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isWordPlusNumber(String str, int minLength, int maxLength) {
        if (minLength < 1) {
            minLength = 1;
        }
        String strPattern = String.format("^[a-zA-Z][a-zA-Z0-9-_]{%d,%d}$", minLength - 1, maxLength);
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 判断字符串是否为email格式
     *
     * @param strEmail
     * @return
     */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    /**
     * 判断字符串是否数字
     *
     * @param str
     * @return
     */
    public static boolean isPhoneNumber(String str) {
        Pattern pattern = Pattern.compile("^\\d{0,20}$");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 判断字符串是否手机号码
     *
     * @param value
     * @return
     */
    public static boolean isMobilePhone(String value) {
        boolean isValidate = value.length() == 11 && value.startsWith("1");
        if (!isValidate) {
            return false;
        }
        String strPattern = "\\d{11}";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(value);
        boolean res = m.matches();
        return res;
    }

    /**
     * 判读字符串是否固定电话或手机号码
     * 格式：只允许数字空格短横线
     *
     * @param value
     * @return
     */
    public static boolean isPhone(String value) {
        if (!isMobilePhone(value)) {
            Pattern p = Pattern.compile("[a-zA-Z]");
            Matcher m = p.matcher(value);
            if (m.matches()) {
                return false;     //包含字母
            }
            p = Pattern.compile("[\u4e00-\u9fa5]");
            m = p.matcher(value);
            if (m.matches()) {
                return false;     //包含汉字
            }
        }

        String patternStr = "[\\d\\s-]+"; //java中反斜杠要转义
        Matcher meq = Pattern.compile(patternStr).matcher(value);
        return meq.matches();
    }
}
