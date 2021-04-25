import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.ptr.FloatByReference;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class VariantTimeParser {


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
        FloatByReference pvtime = new FloatByReference();
        //赋值
        sysTime.wYear =(short) calendar.get(Calendar.YEAR);
        sysTime.wMonth = (short)calendar.get(Calendar.MONTH);
        sysTime.wDayOfWeek = (short)calendar.get(Calendar.DAY_OF_WEEK);
        sysTime.wDay =  (short)calendar.get(Calendar.DAY_OF_MONTH);
        sysTime.wHour =  (short)calendar.get(Calendar.HOUR);
        sysTime.wMinute = (short)calendar.get(Calendar.MINUTE);
        sysTime.wSecond =  (short)calendar.get(Calendar.MINUTE);
        sysTime.wMilliseconds =  (short)calendar.get(Calendar.MILLISECOND);



        CLibrary.INSTANCE.SystemTimeToVariantTime(sysTime,pvtime);

        System.out.println(pvtime.getValue());
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
        int SystemTimeToVariantTime (SYSTEMTIME systemtime, FloatByReference pvtime);
    }

}
