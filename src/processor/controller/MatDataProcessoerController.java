package processor.controller;

import csv.tool.ReadByApachCsv;
import file.getter.MatColGetter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import path.constant.Constant;
import path.constant.SplitCsv;
import select.DirFinder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MatDataProcessoerController {
    private static Logger LOGGER = LogManager.getLogger(MatDataProcessoerController.class.getName());

    public static void main(String[] args) throws IOException, ParseException {

        LOGGER.info("DataProcessor  开始运行");

        String stockPath = Constant.StockPath;
        String outputPath = Constant.MatOutputPath;
        String cspPath = Constant.ListCsvPathWithFlag; //改用带标志位的新文件

        String csvListPath = Constant.ListsCsvPath; //切分的任务文件


        SplitCsv.doSplitWhenInit(); //执行一次带状态位的任务切分

        File splitFilePath = new File(csvListPath);
        File [] csvs = splitFilePath.listFiles();

        //启动4个线程  因为分成了37个文件 按照4组分 这里代码写死了 为每个线程最多分10个
        for(int t=0;t<4;t++){
            List<String> tmpCsvNameList = new ArrayList<String>();
            for(int i=0;i<10;i++){
                int tmpIndex = t*10+i;
                if(tmpIndex < csvs.length){
                    tmpCsvNameList.add(csvs[tmpIndex].getAbsolutePath()); //分组
                }
            }
            Thread tmpThrad = new Thread(new MatThread(tmpCsvNameList),"No."+t);
            tmpThrad.start();
        }

/*        List<String> stockNameList  = ReadByApachCsv.getStockList(cspPath);
        for(int i=0;i<stockNameList.size();i++){
            String aimStock = stockNameList.get(i)+".mat";
            aimstockProcessor(stockPath,aimStock,outputPath);
        }*/




        LOGGER.info("DataProcessor 运行结束");
    }

    public static void aimstockProcessor(String stockPath,String aimStock,String outputPath) throws IOException, ParseException {
        //输出文件
        //000001.SZ.mat
        String [] sp = aimStock.split("\\.");

        String outfileName = sp[0]+"."+sp[1]+".csv";//合成文件名 两个点

        String outfilePath =  outputPath + File.separator + outfileName;
        File outfile = new File(outfilePath);
        if(!outfile.exists()){
            outfile.createNewFile(); //不存在则创建 存在则追加
        }
//        System.out.println("outfile :"+outfilePath);
        //开始合并一个文件
        DirFinder finder = new DirFinder();
        List<File> stockList = finder.findOneStockInAllYearsDir(stockPath,aimStock);
        //输出流  重复打开输出结果文件 优化 只打開一次流
        FileOutputStream fos = new FileOutputStream(outfile,true); //追加模式
        BufferedOutputStream buf = new BufferedOutputStream(fos);

        for(int i=0;i<stockList.size();i++){
            File tmpStock = stockList.get(i);
            MatColGetter.matReader(tmpStock,buf);
        }

        buf.close();
        fos.close();  //一個aim 處理完成後 將流關閉 提升效率

        //更改源list文件的标志位 同步方法
        ReadByApachCsv.writeCompleteFlag(Constant.ListCsvPathWithFlag,aimStock,"completed");
        LOGGER.info(Thread.currentThread().getName()+"process complete aimStock : "+aimStock);
    }




}
