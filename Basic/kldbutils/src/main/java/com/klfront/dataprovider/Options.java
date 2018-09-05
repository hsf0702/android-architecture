package com.klfront.dataprovider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by kl on 2018/6/6.
 * @author  lujinlong
 * @desc
 */

public class Options {

    private static Options instance = null;
    private SharedPreferences preferences = null;

    private Options(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Options getInstance(Context context) {
        // 防止对具体Activity对象的持有
        if (instance == null) {
            instance = new Options(context.getApplicationContext());
        }
        return instance;
    }

    public void setParam(String name, Object value) {
        if (value instanceof String) {
            preferences.edit().putString(name, value.toString()).commit();
        } else if (value instanceof Boolean) {
            preferences.edit().putBoolean(name, (Boolean) value).commit();
        } else if (value instanceof Integer) {
            preferences.edit().putInt(name, Integer.parseInt(value.toString())).commit();
        } else if (value instanceof Long) {
            preferences.edit().putLong(name, Long.parseLong(value.toString())).commit();
        }else if(value instanceof Float){
            preferences.edit().putFloat(name,(Float)value);
        }
    }

    public Object getParam(String name, Object defvalue) {
        if (defvalue instanceof String) {
            return preferences.getString(name, (String) defvalue);
        } else if (defvalue instanceof Boolean) {
            return preferences.getBoolean(name, (Boolean) defvalue);
        } else if (defvalue instanceof Integer) {
            return preferences.getInt(name, (Integer) defvalue);
        } else if (defvalue instanceof Long) {
            return preferences.getLong(name, (Long) defvalue);
        }else if (defvalue instanceof Float) {
            return preferences.getFloat(name, (Float) defvalue);
        }
        return preferences.getString(name, "");
    }

    public void setStringParam(String key, String value) {
        preferences.edit().putString(key, value).commit();
    }

    public String getStringParam(String key) {
        return preferences.getString(key, "");
    }

    public void setBoolParam(String key, boolean value) {
        preferences.edit().putBoolean(key, value).commit();
    }

    public boolean getBoolParam(String key) {
        return preferences.getBoolean(key, false);
    }

    public void setIntParam(String key, int value) {
        preferences.edit().putInt(key, value).commit();
    }

    public int getIntParam(String key) {
        return preferences.getInt(key, 0);
    }

    public void setLongParam(String key, long value) {
        preferences.edit().putLong(key, value).commit();
    }

    public long getLongParam(String key) {
        return preferences.getLong(key, 0);
    }

    public void setFloatParam(String key, float value) {
        preferences.edit().putFloat(key, value).commit();
    }

    public float getFloatParam(String key) {
        return preferences.getFloat(key, 0.0f);
    }
}