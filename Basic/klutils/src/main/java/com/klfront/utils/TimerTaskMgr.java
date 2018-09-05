package com.klfront.utils;

import android.os.Handler;


/**
 * 定时任务管理
 * Created by L on 2017/4/6.
 */
public class TimerTaskMgr {
    public interface IQhTimerTaskListener {
        void executeTimerTask();
    }

    public boolean isRunning() {
        return isRunning;
    }

    private boolean isRunning = false;
    private int interval = 1000 * 60 * 2;//时间间隔：毫秒
    private Handler handler = new Handler();
    private IQhTimerTaskListener listener = null;

    /**
     * struct method
     *
     * @param listener 定时任务监听者
     * @param interval 时间间隔：毫秒
     */
    public TimerTaskMgr(IQhTimerTaskListener listener, int interval) {
        this.listener = listener;
        this.interval = interval;
    }

    /**
     * @param interval interval time(unit of millisecond)
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getInterval() {
        return interval;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            listener.executeTimerTask();
            if(isRunning) {
                handler.postDelayed(this, interval);
            }
        }
    };


    public void start() {
        if (!isRunning) {
            handler.post(runnable);
            isRunning = true;
        }
    }

    /**
     * @param execute 是否立即执行
     */
    public void start(boolean execute) {
        if (execute) {
            handler.post(runnable);
        } else {
            handler.postDelayed(runnable, interval);
        }
        isRunning = true;
    }

    public void stop() {
        if (isRunning) {
            //延时停止定时器，让最后修改的内容有机会完成上传
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handler.removeCallbacks(runnable);
                }
            }, interval);
            isRunning = false;
        }
    }

    public void stopNow() {
        handler.removeCallbacks(runnable);
        isRunning = false;
    }
}
