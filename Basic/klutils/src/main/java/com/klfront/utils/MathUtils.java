package com.klfront.utils;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by dell on 2017/11/30.
 */

public class MathUtils {

    /**
     * 获取矩形内外点连线与矩形边的交点
     *
     * @param outP 矩形范围外点
     * @param inP 矩形范围内点
     * @param rectF 矩形
     * @return 两个点连线与矩形边的交点
     */
    public static PointF getIntersectionOfLineAndRect(PointF outP, PointF inP, RectF rectF) {
        Double MT_EPS = 1e-4;//接近零的参考值

        PointF ret = new PointF(0.0f, 0.0f);
        //top
        PointF topL = new PointF(rectF.left, rectF.top);
        PointF topR = new PointF(rectF.right, rectF.top);
        ret = getIntersectOf2Line(inP, outP, topL, topR);
        if (!(ret.x == 0 && ret.y == 0) && Math.abs(ret.y - rectF.top) < MT_EPS) {
            return ret;
        }

        //bottom
        PointF bottomL = new PointF(rectF.left, rectF.bottom);
        PointF bottomR = new PointF(rectF.right, rectF.bottom);
        ret = getIntersectOf2Line(inP, outP, bottomL, bottomR);
        if (!(ret.x == 0 && ret.y == 0) && Math.abs(ret.y - rectF.bottom) < MT_EPS) {
            return ret;
        }

        //left
        PointF leftT = new PointF(rectF.left, rectF.top);
        PointF leftB = new PointF(rectF.left, rectF.bottom);
        ret = getIntersectOf2Line(inP, outP, leftT, leftB);
        if (!(ret.x == 0 && ret.y == 0) && Math.abs(ret.x - rectF.left) < MT_EPS) {
            return ret;
        }

        //right
        PointF rightT = new PointF(rectF.right, rectF.top);
        PointF rightB = new PointF(rectF.right, rectF.bottom);
        ret = getIntersectOf2Line(inP, outP, rightT, rightB);
        if (!(ret.x == 0 && ret.y == 0) && Math.abs(ret.x - rectF.right) < MT_EPS) {
            return ret;
        }

        return ret;
    }

    /**
     * 求两条线的交点
     *
     * @param startPoint1 第一条线段起点
     * @param endPoint1  第一条线段终点
     * @param startPoint2 第二条线段起点
     * @param endPoint2 第二条线段终点
     * @return
     */
    public static PointF getIntersectOf2Line(PointF startPoint1, PointF endPoint1, PointF startPoint2, PointF endPoint2) {
        Float mua, mub;
        Float denom, numera, numerb;

        Float x1 = startPoint1.x;
        Float y1 = startPoint1.y;
        Float x2 = endPoint1.x;
        Float y2 = endPoint1.y;

        Float x3 = startPoint2.x;
        Float y3 = startPoint2.y;
        Float x4 = endPoint2.x;
        Float y4 = endPoint2.y;

        denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        numera = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
        numerb = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3);

        Double MT_EPS = 1e-4;
        /* Are the lines coincident? */
        if (Math.abs(numera) < MT_EPS && Math.abs(numerb) < MT_EPS && Math.abs(denom) < MT_EPS) {
            return new PointF((x1 + x2) / 2.0f, (y1 + y2) / 2.0f);
        }

	    /* Are the line parallel */
        if (Math.abs(denom) < MT_EPS) {
            return new PointF(0, 0);
        }

	    /* Is the intersection along the the segments */
        mua = numera / denom;
        mub = numerb / denom;
        if (mua < 0 || mua > 1 || mub < 0 || mub > 1) {
            return new PointF(0, 0);
        }

        return new PointF(x1 + mua * (x2 - x1), y1 + mua * (y2 - y1));
    }


    public static float ToFixed3(float value) {
        return (float) (Math.round(value * 1000)) / 1000;
    }

    public static float ToFixed6(float value) {
        return (float) (Math.round(value * 1000000)) / 1000000;
    }
}
