package com.usim.ulib.notmine.alg;
/**
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130112
 */
public class ElapsedTime
{
    public ElapsedTime() {}

    public static long systemTime()
    {
        return System.nanoTime();
    }

    public static double calcElapsed(long start, long end) 
    {   double elapsed = (end - start) / 1.0e+9;
        return elapsed;
    }

    public static String calcElapsedHMS(long start, long end) 
    {   String s = "";
        double elapsed = (end - start) / 1.0e+9;
        s = s + calcHMS((int) Math.round(elapsed));
        return s;
    }

    public static String calcHMS(int timeInSeconds) 
    {   String s = "";
        int hours, minutes, seconds;
        hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds - (hours * 3600);
        minutes = timeInSeconds / 60;
        timeInSeconds = timeInSeconds - (minutes * 60);
        seconds = timeInSeconds;  
        s = s + hours + "h " + minutes + "m " + seconds + "s";
        return s;
    }

    public static void doPause(int timeInSeconds) 
    {   long t0, t1;
        t0 = System.currentTimeMillis();
        t1 = System.currentTimeMillis() + (timeInSeconds * 1000);
        do 
        {   t0 = System.currentTimeMillis();
        } while (t0 < t1);
    }
}
