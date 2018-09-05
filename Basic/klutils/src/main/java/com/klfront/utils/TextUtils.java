package com.klfront.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L on 2016/11/23.
 */

public class TextUtils {
    public static String listToString(List<String> stringList,String split){
        if (stringList==null) {
            return null;
        }
        StringBuilder result=new StringBuilder();
        boolean isfirst=true;
        for (String string : stringList) {
            if (isfirst) {
                isfirst=false;
            }else {
                result.append(split);
            }
            result.append(string);
        }
        return result.toString();
    }

    public static String listToString(List<String> stringList){
        return listToString(stringList,",");
    }

    public static List<String> stringToList(String str,String split){
        if (str==null||str.length()==0) {
            return null;
        }
        String[] arr =str.split(split);
        List<String> list = new ArrayList<String>();
        for (String s: arr) {
            list.add(s);
        }
        return list;
    }

    public static List<String> stringToList(String str){
        return stringToList(str,",");
    }
}
