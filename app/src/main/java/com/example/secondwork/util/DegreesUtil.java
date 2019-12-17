package com.example.secondwork.util;

import android.view.MotionEvent;

import java.util.List;

public class DegreesUtil {
    public static double getActionDegrees(float x, float y, float x1, float y1, float x2, float y2)
    {

        double a = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        double b = Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
        double c = Math.sqrt((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y));
        // 余弦定理
        double cosA = (b * b + c * c - a * a) / (2 * b * c);
        // 返回余弦值为指定数字的角度，Math函数为我们提供的方法
        double arcA = Math.acos(cosA);
        double degree = arcA * 180 / Math.PI;

        // 接下来我们要讨论正负值的关系了，也就是求出是顺时针还是逆时针。
        // 第1、2象限
        if (y1 < y && y2 < y)
        {
            if (x1 < x && x2 > x)
            {// 由2象限向1象限滑动
                return degree;
            }
            // 由1象限向2象限滑动
            else if (x1 >= x && x2 <= x)
            {
                return -degree;
            }
        }
        // 第3、4象限
        if (y1 > y && y2 > y)
        {
            // 由3象限向4象限滑动
            if (x1 < x && x2 > x)
            {
                return -degree;
            }
            // 由4象限向3象限滑动
            else if (x1 > x && x2 < x)
            {
                return degree;
            }

        }
        // 第2、3象限
        if (x1 < x && x2 < x)
        {
            // 由2象限向3象限滑动
            if (y1 < y && y2 > y)
            {
                return -degree;
            }
            // 由3象限向2象限滑动
            else if (y1 > y && y2 < y)
            {
                return degree;
            }
        }
        // 第1、4象限
        if (x1 > x && x2 > x)
        {
            // 由4向1滑动
            if (y1 > y && y2 < y)
            {
                return -degree;
            }
            // 由1向4滑动
            else if (y1 < y && y2 > y)
            {
                return degree;
            }
        }

        // 在特定的象限内
        float tanB = (y1 - y) / (x1 - x);
        float tanC = (y2 - y) / (x2 - x);
        if ((x1 > x && y1 > y && x2 > x && y2 > y && tanB > tanC)// 第一象限
                || (x1 > x && y1 < y && x2 > x && y2 < y && tanB > tanC)// 第四象限
                || (x1 < x && y1 < y && x2 < x && y2 < y && tanB > tanC)// 第三象限
                || (x1 < x && y1 > y && x2 < x && y2 > y && tanB > tanC))// 第二象限
            return -degree;
        return degree;
    }
    public static float getDistanceOfFingue(MotionEvent event){
        float dis_x = Math.abs(event.getX(0) - event.getX(1));
        float dis_y = Math.abs(event.getY(0) - event.getY(1));
        float cur_dis = (float) Math.sqrt(dis_x*dis_x+dis_y*dis_y);
        return cur_dis;
    }
    public static boolean getNode(float x1,float y1
                                 ,float x2,float y2
                                 ,float x3,float y3
                                 ,float x4,float y4
                                  ,float[] node){
        //如果没有交点，返回false,两条直线平行，斜率存在且相同；斜率都不存在
        //否则返回true,node存储交点坐标
        if((x1-x2)==0&&(x3-x4)==0) {
            return false;
        }
        if((x1-x2)==0||(x3-x4)==0){
            if(x1-x2!=0){
                node[0] = x3;
                float k1 = (y2-y1)/(x2-x1);
                float b1 = (y1*x2-y2*x1)/(x2-x1);
                float y = k1*x3+b1;
                node[1] = y;
            }
            if(x3-x4!=0){
                node[0] = x1;
                float k2 = (y4-y3)/(x4-x3);
                float b2 = (y3*x4-y4*x3)/(x4-x3);
                float y = k2 * x1 + b2;
                node[1] = y;
            }
            return true;
        }else{
            float k1 = (y2-y1)/(x2-x1);
            float b1 = (y1*x2-y2*x1)/(x2-x1);
            float k2 = (y4-y3)/(x4-x3);
            float b2 = (y3*x4-y4*x3)/(x4-x3);
            //斜率都存在
            //斜率相同
            if(k1==k2){
                return false;
            }
            //斜率不同
            float x = (b1-b2)/(k2 - k1);
            float y = (k2*b1-k1*b2)/(k2-k1);
            node[0] = x;
            node[1] = y;
            return true;
        }
    }
}
