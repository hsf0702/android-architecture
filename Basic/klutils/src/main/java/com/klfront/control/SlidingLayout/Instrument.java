package com.klfront.control.SlidingLayout;

import android.view.View;

/**
 * Created by Linhh on 16/5/24.
 */
public class Instrument
{
    private static Instrument mInstrument;

    public static Instrument getInstance()
    {
        if (mInstrument == null)
        {
            mInstrument = new Instrument();
        }
        return mInstrument;
    }

    public float getTranslationY(View view)
    {
        if (view == null)
        {
            return 0;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
        {
            return view.getTranslationY();
        }
        else
        {
            return view.getTranslationY();
        }
    }

    public void slidingByDelta(View view, float delta)
    {
        if (view == null)
        {
            return;
        }
        view.clearAnimation();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
        {
            view.setTranslationY(delta);
        }
        else
        {
            view.setTranslationY(delta);
        }
    }

    public void slidingToY(View view, float y)
    {
        if (view == null)
        {
            return;
        }
        view.clearAnimation();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
        {
            view.setY(y);
        }
        else
        {
            view.setY(y);
        }
    }

    public void reset(View view, long duration)
    {
        if (view == null)
        {
            return;
        }
        view.clearAnimation();
        android.animation.ObjectAnimator.ofFloat(view, "translationY", 0F).setDuration(duration).start();
    }

    public void smoothTo(View view, float y, long duration)
    {
        if (view == null)
        {
            return;
        }
        view.clearAnimation();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
        {
            android.animation.ObjectAnimator.ofFloat(view, "translationY", y).setDuration(duration).start();
        }
    }
}
