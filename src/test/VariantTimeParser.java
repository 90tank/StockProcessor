package test;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.ptr.DoubleByReference;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * 变体时间转换类
 * 注意 年+1900
 */
public class VariantTimeParser {

    /**
     * test this method ok ,2019/03/30
     * @param args
     */
   /* public static void main(String[] args) {
        double vt = SystemTimeToVarinantTime(System.currentTimeMillis());
        System.out.println("test vt is :"+ vt);

    }*/


    public static double SystemTimeToVarinantTime(long timeStamp){
        //实例化timeStamp 为一个日历
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); //东八区
        calendar.setTimeInMillis(timeStamp);

        //时间结构体 c++方法入参
        CLibrary.SYSTEMTIME sysTime = new CLibrary.SYSTEMTIME();
        //返回值 变体时间
        DoubleByReference pvtime = new DoubleByReference();
        //赋值
        sysTime.wYear =(short) (calendar.get(Calendar.YEAR)+1900);
        sysTime.wMonth = (short)calendar.get(Calendar.MONTH);
        //sysTime.wDayOfWeek = (short)calendar.get(Calendar.DAY_OF_WEEK);
        sysTime.wDay =  (short)calendar.get(Calendar.DAY_OF_MONTH);
        sysTime.wHour =  (short)calendar.get(Calendar.HOUR);
        sysTime.wMinute = (short)calendar.get(Calendar.MINUTE);
        sysTime.wSecond =  (short)calendar.get(Calendar.SECOND);
        sysTime.wMilliseconds =  (short)calendar.get(Calendar.MILLISECOND);

        //转换方法
        CLibrary.INSTANCE.SystemTimeToVariantTime(sysTime,pvtime);

        return pvtime.getValue();
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

        int SystemTimeToVariantTime(SYSTEMTIME systemtime, DoubleByReference pvtime);
        int VariantTimeToSystemTime(double vtime, SYSTEMTIME lpSystemTime);
    }

}
