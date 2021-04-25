package path.constant;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SplitCsv {


    /**
     * 所有线程启动之前的 原始股票文件名任务分配
     * 因为代码不会主动的给且纷纷开的文件中添加状态标志 当代码挂掉后重启时 ，进行切分 可以获取正确的标志位信息
     * @throws IOException
     */
    public static void doSplitWhenInit() throws IOException {

        String destPath   = Constant.ListsCsvPath;

        List<String> stockList = new ArrayList<>(4000);
        stockList = getStockListWithFlag( Constant.ListCsvPathWithFlag);

        int size100 = stockList.size()/100;
        int left =  stockList.size()%100;

        //按100个股票 1个文件名院士文件
        for(int i=0;i<size100;i++){

            File tmpCsvNameList = new File(destPath+File.separator+"list"+i+".csv");
            if(!tmpCsvNameList.exists()){
                tmpCsvNameList.createNewFile();
            }

            StringBuilder sb = new StringBuilder();
            for(int k=0;k<100;k++){
                sb.append(stockList.get(100*i+k));
            }

            FileOutputStream fos  = new FileOutputStream(tmpCsvNameList,false); //不追加 直接覆盖
            fos.write(sb.toString().getBytes());
            fos.close();
        }



        //剩余不足100的 单独拿出来
        File leftCsvNameList = new File(destPath+File.separator+"list"+size100+".csv");
        if(!leftCsvNameList.exists()){
            leftCsvNameList.createNewFile();
        }

        StringBuilder sb = new StringBuilder();
        for(int i=0;i<left;i++){

            sb.append(stockList.get(100*size100+i)).append("\n");

            FileOutputStream fos  = new FileOutputStream(leftCsvNameList);
            fos.write(sb.toString().getBytes());
            fos.close();
        }

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
     * 获取股票名 处理标志
     * @param csvPath
     * @return
     * @throws IOException
     */
    public static List<String> getStockListWithFlag(String csvPath) throws IOException {
        Reader in = new FileReader(csvPath);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
        List<String> stockList = new ArrayList<>(4000);

        for (CSVRecord record : records) {
            String stockName = record.get(0);
            stockName = stockName.replaceAll("'","");
            String thisLine = stockName+","+record.get(1)+"\n";

            stockList.add(thisLine);
        }
        return stockList;
    }

}
