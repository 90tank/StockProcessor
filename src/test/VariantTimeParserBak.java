package test;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;

import java.util.Calendar;
import java.util.TimeZone;


public class VariantTimeParserBak {


    public static float SystemTimeToVarinantTime(){
        FloatByReference pvtime = new FloatByReference();
        return pvtime.getValue();
    }


    public static void main(String[] args) {

        long time = System.currentTimeMillis();
        //实例化一个日历
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); //东八区
        calendar.setTimeInMillis(time);
        CLibrary.SYSTEMTIME sysTime = new CLibrary.SYSTEMTIME();
        CLibrary.SYSTEMTIME sysTime2 = new CLibrary.SYSTEMTIME();
        DoubleByReference pvtime = new DoubleByReference();
        //赋值
        /*sysTime.wYear =(short) calendar.get(Calendar.YEAR);
        sysTime.wMonth = (short)calendar.get(Calendar.MONTH);
        sysTime.wDayOfWeek = (short)calendar.get(Calendar.DAY_OF_WEEK);
        sysTime.wDay =  (short)calendar.get(Calendar.DAY_OF_MONTH);
        sysTime.wHour =  (short)calendar.get(Calendar.HOUR);
        sysTime.wMinute = (short)calendar.get(Calendar.MINUTE);
        sysTime.wSecond =  (short)calendar.get(Calendar.SECOND);
        sysTime.wMilliseconds =  (short)calendar.get(Calendar.MILLISECOND);
*/
        // 转换时 要给年加1900    XXXXX
        //sysTime.wYear =2019 +1900;//因时间起点不同 所以加1900（Matlab的时间起点是1900 ，2019 用数值3919表示）
        sysTime.wYear =2010;

        sysTime.wMonth = 2;
        sysTime.wDay =  1;
        //84120000
        sysTime.wHour =  8;
        sysTime.wMinute = 41;
        sysTime.wSecond =  20;  //2010-02-01 08：41：20

        sysTime.wMilliseconds = 0;
        CLibrary.INSTANCE.SystemTimeToVariantTime(sysTime,pvtime);
        System.out.println(" time "+pvtime.getValue());
        System.out.println("The value 2.0 represents January 1, 1900");


        double vtime= 2.0; //The value 2.0 represents January 1, 1900
        CLibrary.INSTANCE.VariantTimeToSystemTime(vtime,sysTime2);

        System.out.println("--->"+(sysTime2.wYear));  // 时间起点是1900
        System.out.println("--->"+sysTime2.wMonth);
        System.out.println("--->"+sysTime2.wDay);
        System.out.println("wDayOfWeek--->"+sysTime2.wDayOfWeek);
        System.out.println("--->"+sysTime2.wHour);
        System.out.println("--->"+sysTime2.wMinute);
        System.out.println("--->"+sysTime2.wSecond);








    }

    public static interface CLibrary extends Library
    {
        @Structure.FieldOrder({ "wYear", "wMonth", "wDayOfWeek", "wDay", "wHour", "wMinute", "wSecond", "wMilliseconds" })
        public static class SYSTEMTIME extends Structure {
            public short wYear;
            public short wMonth;
            public short wDayOfWeek;
            public short wDay;
            public short wHour;
            public short wMinute;
            public short wSecond;
            public short wMilliseconds;
        }

        CLibrary INSTANCE = (CLibrary) Native.loadLibrary("oleaut32",CLibrary.class);
//        int SystemTimeToVariantTime (SYSTEMTIME systemtime, FloatByReference pvtime);
        int SystemTimeToVariantTime(SYSTEMTIME systemtime, DoubleByReference pvtime);
        int VariantTimeToSystemTime(double vtime, SYSTEMTIME lpSystemTime);
    }

}
