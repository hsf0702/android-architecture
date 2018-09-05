package com.klfront.utils;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by dell on 2017/5/27.
 */

public class AnimationUtils {
    /**
     * 获取动画
     *
     * @param isFromRight true 从右侧进入 false 从左侧进入
     * @return
     */
    public static Animation initAnimation(boolean isFromRight) {
        //开始动画效果
        Animation animation;
        if (isFromRight) {
            //从右侧进入的动画效果
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        } else {
            //从左侧进入的动画效果
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        }
        animation.setDuration(200);
        animation.setFillAfter(true);
        animation.setInterpolator(new AccelerateInterpolator());
        return animation;
    }

    /**
     * 垂直动画
     * fromYValue和toYValue取值及效果：
     * 从view顶部向下进入 -1.0，0
     * 从view底部向下移出 0,1.0
     * 从view底部向上进入 1.0,0
     * 从view顶部向上移出 0，-1.0
     *
     * @param view 执行动画的视图
     * @param fromYValue 移动起始位置： -1.0 表示从向上偏离view的高度位置，1.0 表示向下偏离view的高度位置，0表示从view的原始位置开始
     * @param toYValue   移动目标位置 -1.0 表示从向上偏离view的高度位置，1.0 表示向下偏离view的高度位置，0表示从view的原始位置开始
     * @param duration   动画持续时间 milliseconds
     */
    public static void startVerticalAnimation(View view, float fromYValue, float toYValue,int duration) {
        Animation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                fromYValue, Animation.RELATIVE_TO_SELF, toYValue);
        mShowAction.setDuration(360);
        view.startAnimation(mShowAction);
    }
}
