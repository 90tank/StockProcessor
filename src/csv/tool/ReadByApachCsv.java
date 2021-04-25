package csv.tool;

import file.getter.MatColGetter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import path.constant.Constant;
import processor.controller.CsvDataProcessorController;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReadByApachCsv {

    public static long lastTime = 0;
    private static Logger LOGGER = LogManager.getLogger(ReadByApachCsv.class.getName());

    /**
     * 传入csv文件集合,解析并追加
     */
    public static void readAllAimCsv(List<File> csvList,File appendResultFile) throws FileNotFoundException {
        //输出流  重复打开输出结果文件 可优化
        FileOutputStream fos = new FileOutputStream(appendResultFile,true); //追加模式
        BufferedOutputStream buf = new BufferedOutputStream(fos);

        for(int i=0;i<csvList.size();i++){
            File csv = csvList.get(i);
            readTheCsv(csv,buf);
        }

        try {
            buf.close();
            fos.close();
        } catch (IOException e) {
            LOGGER.error("error when close io ",e);
        }
    }


     public static void readTheCsv(File csv, BufferedOutputStream buf){
         ReadByApachCsv.lastTime = 0; //保证下一个文件读取不会受影响

         if (csv==null){
             return;
         }
         LOGGER.info("current csv : "+csv.getAbsolutePath());
         Reader in = null;
         Iterable<CSVRecord> csvRecords = null;
         //List<CSVRecord> csvRecords = null;
         try {
           /*  String fileHeaders[]
                     =
                     {"日期","时间","成交价","成交量","总量",	"额",	"B1价",	"B1量",	"B2价","B2量",	"B3价",	"B3量",	"B4价",	"B4量",	"B5价",	"B5量",	"S1价","S1量","S2价","S2量","S3价","S3量","S4价","S4量",	"S5价",	"S5量","BS"};
             CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(fileHeaders);
             FileInputStream fis = new FileInputStream(csv);
             BufferedReader br = new BufferedReader(new InputStreamReader(fis, "gbk"));//解决乱码问题
             CSVParser csvFileParser = new CSVParser(br,csvFileFormat);*/


            // csvRecords = csvFileParser.getRecords();
/*             //获取行
             CSVRecord  rrd =  csvRecords.get(0);
             //获取列
             String gb = rrd.get(0);*/

/*             LOGGER.info("rrd "+rrd.toString());
             LOGGER.info("gb "+gb);*/



             in = new FileReader(csv);
             csvRecords = CSVFormat.RFC4180.parse(in);


         } catch (FileNotFoundException e) {
             LOGGER.error("FileNotFoundException "+csv.getAbsolutePath());
             return ;
         } catch (IOException e) {
             LOGGER.error("csv parse error "+csv.getAbsolutePath(),e);
             return ;
         }

         double lastPrice = -1;
         for (CSVRecord record : csvRecords) {
            long index =  record.getRecordNumber();
            if(index == 1){
                continue;
            }
             String date = record.get(0);
             String time = record.get(1);

            /* if(time.length()==8){
                 time = "0"+time; //Time 宽度对齐
             }*/

             long timeStamp = judgeDateInScope(date,time);

             if(timeStamp<0){
                 continue; //不在时间范围则丢弃本条记录
             }

             if(timeStamp - ReadByApachCsv.lastTime <=0 ){
                 continue; //时间倒退 或者 时间重复 则 丢弃本条数据
             }

             ReadByApachCsv.lastTime = timeStamp; // 更新上一个时间

             StringBuilder sb = new StringBuilder();
             double variantTime = MatColGetter.SystemTimeToVarinantTime(timeStamp);
             variantTime +=  693960 ;

             //成交价
             String pri = record.get(2);
             //成交量 要求乘以100
             String vol = record.get(3) ;


             boolean flag940 = cmp940Time(date,time);
             double PriValue = Double.valueOf(pri);


             int intVol = 0;
             try {
                 intVol = Integer.valueOf(vol);
             } catch (NumberFormatException e) {
                 continue; //扔掉本条 090520
             }


             /*价格==0 ，异常处理开始*/
             if(PriValue==0 && !flag940 ){
                 continue; //扔掉本条
             }

             if(PriValue==0 &&  flag940 ){

                 if(intVol<=0){
                     continue; //成交量为0 价格也为0，扔掉本条
                 }else{
                     if(lastPrice < 0){ //成交量为有效值（大于0） 如果上一个有效值仍然为初始值 则继续丢弃
                         continue; //扔掉本条
                     }
                     //使用上一个有效值
                     pri = lastPrice + "";
                 }
             }

             //非异常价格，更新lastPrice 以备后患
             if( PriValue>0){
                 lastPrice = PriValue;
             }
             Long vol_long = Long.valueOf(vol)*100; //成交量 100

             sb.append(variantTime).append(",").append(pri).append(",").append(vol_long);

             //b1价 - b5价  6 8 10 12 14
             //b1量 - b5量  7 9 11 13 15 要求乘以100
             //s1价 - s5价  16 18 20 22 24
             //s1量 - s5量  17 19 21 23 25 要求乘以100

             for(int i=6;i<=14;){
                 sb.append(",").append(record.get(i));
                 i+=2;
             }

             for(int i=7;i<=15;){
                 sb.append(",").append(Long.valueOf(record.get(i))*100);
                 i+=2;
             }

             for(int i=16;i<=24;){
                 sb.append(",").append(record.get(i));
                 i+=2;
             }

             for(int i=17;i<=25;){
                 sb.append(",").append(Long.valueOf(record.get(i))*100);
                 i+=2;
             }
             sb.append("\n");

             try {

                 buf.write(sb.toString().getBytes());
                 buf.flush();
             } catch (IOException e) {
                 LOGGER.error("IOException when write buff ",e);
             }

         }


     }







    /**
     * 时间区间判断
     * @param date
     * @param time
     * @return
     */
    public static long judgeDateInScope(String  date,String time){
         String dateTime = date+" "+time;
         //9:30-11:30 , 13：00-15:00
         String time0930 = date+" "+"09:30:00";
         String time1130 = date+" "+"11:30:00";
         String time1300 = date+" "+"13:00:00";
         String time1500 = date+" "+"15:00:00";
         //2016/1/4 8:47:12
         SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
         //yyyy MM dd HH mmssSSS
         //2010 01 04 3 0 0 00
         long long930 = 0;
         long long1130 = 0;
         long long1300 = 0;
         long long1500 = 0;
         Date dateparse = null;
         try {
             long930 = sf.parse(time0930).getTime();
             long1130 = sf.parse(time1130).getTime();
             long1300 = sf.parse(time1300).getTime();
             long1500 = sf.parse(time1500).getTime();
         } catch (ParseException e) {
             //如果日期转换异常则不要这一条数据
             LOGGER.error("data parse error  date :"+date +" time :"+time+"\n",e);
             return -1;
         }

        try {
            dateparse = sf.parse(dateTime);
        } catch (ParseException e) {
            LOGGER.error("sf data parse error  date :"+date +" time :"+time+"\n",e);
            try {
                dateparse = sf2.parse(dateTime);
            } catch (ParseException e1) {
                LOGGER.error("sf2 data parse error  date :"+date +" time :"+time+"\n",e);

            }
        }

         long thisTime = dateparse.getTime(); //时间戳 这一行的时间
         boolean flag = (thisTime>=long930&thisTime<=long1130)||(thisTime>=long1300 & thisTime<=long1500);
         if(!flag){
             return -1;
         }



         return thisTime;
     }


    /**
     * 时间区间判断
     * true  大于等于9:40
     * false 小于等于9:40 (或转换异常)
     * @param date
     * @param time
     * @return
     */
    public static boolean cmp940Time(String  date,String time){
        String dateTime = date+" "+time;
        //9:30-11:30 , 13：00-15:00
        String time0940 = date+" "+"09:40:00";

        //2016/1/4 8:47:12
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        //yyyy MM dd HH mmssSSS
        //2010 01 04 3 0 0 00
        long long940 = 0;

        Date dateparse = null;
        try {
            long940 = sf.parse(time0940).getTime();
        } catch (ParseException e) {
            //如果日期转换异常则不要这一条数据
            LOGGER.error("data parse error  date :"+date +" time :"+time+"\n",e);
            return false;
        }

        try {
            dateparse = sf.parse(dateTime);
        } catch (ParseException e) {
            LOGGER.error("sf data parse error  date :"+date +" time :"+time+"\n",e);
            try {
                dateparse = sf2.parse(dateTime);
            } catch (ParseException e1) {
                LOGGER.error("sf2 data parse error  date :"+date +" time :"+time+"\n",e);
            }
        }

        long thisTime = dateparse.getTime(); //时间戳 这一行的时间


        boolean flag = true;
        if(thisTime<long940){
            return false;
        }

        return flag;
    }



    /**
     * 获取股票名 信息列表
     * @param csvPath
     * @return
     * @throws IOException
     */
    public static List<String> getStockList(String csvPath) throws IOException {
        Reader in = new FileReader(csvPath);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
        List<String> stockList = new ArrayList<>(4000);

        for (CSVRecord record : records) {
            String stockName = record.get(0);
            stockName = stockName.replaceAll("'","");
            stockList.add(stockName);
        }
        return stockList;
    }


    /**
     * 获取没有处理的股票名 信息列表
     * @param csvPath
     * @return
     * @throws IOException
     */

    public static List<String> getUnCompleteStockList(String csvPath) throws IOException {
        Reader in = new FileReader(csvPath);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
        List<String> stockList = new ArrayList<>(4000);

        for (CSVRecord record : records) {

            String stockName = null;
            String flag = null;
            try {
                stockName = record.get(0);
                if(stockName.isEmpty()){
                    continue;
                }
                flag = record.get(1);
            } catch (Exception e) {
                LOGGER.info("csvPath "+csvPath+"stockName "+stockName + "flag"+flag);
            }


            if(flag.equalsIgnoreCase("no")){
                stockName = stockName.replaceAll("'","");
                stockList.add(stockName);
            }
        }
        return stockList;
    }


    /**
     * 暴力的重新更新一次文件
     * @param listWithFlagCsvPath
     */
    synchronized public static void writeCompleteFlag(String listWithFlagCsvPath,String stockName,String completeFlag) throws IOException {


        stockName = stockName.substring(0,stockName.lastIndexOf(".")); //600670.SH.mat --> 600670.SH 去后缀操作

        File completeStockPath = new File(Constant.MatOutputPath);  //获取已经完成的
        HashMap<String,String> completeMap= new LinkedHashMap<>();

        //取源list文件

        Reader in = new FileReader(listWithFlagCsvPath);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);

        for (CSVRecord record : records) {
            String stockNameKey = record.get(0);
            String flag = record.get(1);
            completeMap.put(stockNameKey,flag);
        }
        in.close();   //完成文件读取


        completeMap.put(stockName,completeFlag);   //更新该股票标志位

        //写一个备份的csv文件 写完成后再copy
        File outfile = new File(Constant.ListCsvPathWithFlagBak);

        if(!outfile.exists()){
            outfile.createNewFile(); //不存在则创建
        }
        FileOutputStream fos = new FileOutputStream(outfile,false);//不追加
        BufferedOutputStream buf = new BufferedOutputStream(fos);
        for(Map.Entry<String,String> entry : completeMap.entrySet()){
            StringBuilder sb = new StringBuilder();
            sb.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
            buf.write(sb.toString().getBytes());

            buf.flush();
        }

        buf.close();
        fos.close();

        File listCsvPathWithFlag = new File(Constant.ListCsvPathWithFlag);
        if(listCsvPathWithFlag.exists()){
            listCsvPathWithFlag.delete();  //如果文件存在 则删除   （形成一个简单的备份机制）
        }
        outfile.renameTo(listCsvPathWithFlag); //执行成功后 本文件删除
    }

}
