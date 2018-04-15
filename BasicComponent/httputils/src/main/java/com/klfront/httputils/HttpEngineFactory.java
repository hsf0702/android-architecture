package com.klfront.httputils;

import android.content.Context;

import com.klfront.httputils.interfaces.IHttpEngine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class HttpEngineFactory {
    private static Map<String, Object> mEngineCache = new HashMap<>();

    public static IHttpEngine getHttpEngine(Context context, String className) {
        if(!mEngineCache.containsKey(className))
        {
            try {
                Class engine = Class.forName(className);
                try {
                    Method method = engine.getMethod("getInstance", Context.class);
                    try {
                        Object obj = method.invoke(null, context);
                        if (obj instanceof IHttpEngine) {
                            mEngineCache.put(className,obj);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(mEngineCache.containsKey(className)) {
            return (IHttpEngine) mEngineCache.get(className);
        }
        return null;
    }
}
