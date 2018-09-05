package com.klfront.utils;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by L on 2015/12/11.
 */
public class Colors
{
    static String[] values = new String[]{
            "ffa34e", "3cb9ff", "81d160", "e58b8b", "bb8f6a", "838bd7", "7695af", "edbf38", "5493d7", "5ebb81",
            "d16464", "957c66", "8978a9", "919da9"
    };
    static Random r = new Random(13);

    public static int GetRandomColor()
    {
        return GetColor(r.nextInt(14));
    }

    public static int GetColor(int index)
    {
        if (index > 13)
        {
            index = index % 14;
        }
        String value = values[index];
        int r = Integer.parseInt(value.substring(0, 2), 16);
        int g = Integer.parseInt(value.substring(2, 4), 16);
        int b = Integer.parseInt(value.substring(4, 6), 16);
        return Color.rgb(r, g, b);
    }
}
