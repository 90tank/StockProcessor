package csv.tool;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import path.constant.Constant;

import java.io.*;
import java.util.*;

public class WriteFlagStockList {


    public static void main(String[] args) throws IOException {
        getLeftStockList();
    }

    public static List<String> getLeftStockList() throws IOException {




        File completeStockPath = new File(Constant.MatOutputPath);  //获取已经完成的
        File [] flies = completeStockPath.listFiles();
        String [] completeStockAry = new String[flies.length];
        HashMap<String,String> completeMap= new LinkedHashMap<>();

        for(int i=0;i<flies.length;i++){
            String tmpName = flies[i].getName();
            tmpName = tmpName.substring(0,tmpName.lastIndexOf("."));
            completeStockAry[i] =  tmpName;

            completeMap.put(tmpName,"completed");


        }


        Reader in = new FileReader(Constant.ListCsvPath);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
        List<String> stockList = new ArrayList<>(4000);

        for (CSVRecord record : records) {
            String stockName = record.get(0);
            stockName = stockName.replaceAll("'","");

            String flag = completeMap.get(stockName);
            if(flag==null||!flag.equalsIgnoreCase("completed")){
                completeMap.put(stockName,"no");
            }
        }


        //map 变String ,写新的csv
        File outfile = new File(Constant.ListCsvPathWithFlag);
        if(!outfile.exists()){
            outfile.createNewFile(); //不存在则创建 存在则追加
        }
        FileOutputStream fos = new FileOutputStream(outfile);
        BufferedOutputStream buf = new BufferedOutputStream(fos);

        for(Map.Entry<String,String> entry : completeMap.entrySet()){
            StringBuilder sb = new StringBuilder();
            sb.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
            buf.write(sb.toString().getBytes());

            buf.flush();
        }

        buf.close();
        fos.close();




        return stockList;
    }
}
