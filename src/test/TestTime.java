package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestTime {


    public static void main(String[] args) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date0 = sdf.parse("0000-01-01");
        Date date1 = sdf.parse("1900-01-01");

        long gap = date1.getTime() - date0.getTime();
        long a = gap/(1000*60*60*24);
        System.out.println(a);



    }
}
