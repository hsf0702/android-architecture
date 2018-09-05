package com.klfront.control;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by L on 2017/4/20.
 * 兼容超链接点击事件和右键菜单事件的文本控件
 */
public class TextViewFixTouchConsume extends AppCompatTextView {
    public TextViewFixTouchConsume(Context context) {
        super(context);
    }

    public TextViewFixTouchConsume(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewFixTouchConsume(Context context, AttributeSet attrs,
                                   int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // linkHit = false;
//        boolean res = super.onTouchEvent(event);
//
//        if (dontConsumeNonUrlClicks)
//            return linkHit;
//        return res;

        if (getMovementMethod() == null) {
            boolean result = super.onTouchEvent(event);
            return result;
        }
        MovementMethod m = getMovementMethod();
        setMovementMethod(null);
        boolean mt = m.onTouchEvent(this, (Spannable) getText(), event);
//        if (mt && event.getAction() == MotionEvent.ACTION_DOWN) {
//            event.setAction(MotionEvent.ACTION_UP);
//            mt = m.onTouchEvent(this, (Spannable) getText(), event);
//            event.setAction(MotionEvent.ACTION_DOWN);
//        }
        boolean st = super.onTouchEvent(event);
        setMovementMethod(m);
        setFocusable(false);
        return st||mt;
    }

    @Override
    public boolean hasFocusable() {
        return false;
    }

}

