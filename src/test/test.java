package test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class test {

    private static Logger LOGGER = LogManager.getLogger(test.class.getName());
    /*public static void main(String[] args) throws IOException {
        LOGGER.info("Logger is Logger");
        for(int i=0;i<3;i++){
            File file  = new File("123.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream("123.txt",true); //追加模式
            BufferedOutputStream buf = new BufferedOutputStream(fos);
            buf.write("123".getBytes());


            buf.flush();
            buf.close();
            fos.close();



        }

    }*/


    public static void main(String[] args) throws ParseException {


        String name = "000001.SZ";
        name = name.substring(0,name.length()-3);
        System.out.println(name);
        //2016/1/4 8:47:12
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        Date date = sf.parse("2016/1/4 8:47:12");

        System.out.println(date);
    }
}
