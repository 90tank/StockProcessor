package test;

import csv.tool.ReadByApachCsv;
import file.getter.MatColGetter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import select.DirFinder;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class TestDataProcessoerController {
    private static Logger LOGGER = LogManager.getLogger(TestDataProcessoerController.class.getName());

    public static void main(String[] args) throws IOException, ParseException {

        LOGGER.info("DataProcessor  开始运行");

        String aiStock = "D:\\原始数据\\stock\\tickab201004\\000002.SZ.mat";
        String outputPath = "D:\\原始数据\\OutPutTest";
        String stockPath = "D:\\原始数据\\stock";
        DirFinder finder = new DirFinder();
        String cspPath = "E:\\Run\\DataProcessor\\src\\list.csv";
        List<String> stockNameList  = ReadByApachCsv.getStockList(cspPath);
        String stockName = stockNameList.get(0)+".mat";
        //LOGGER.info("stockName1 : "+stockName);
        //LOGGER.info("stockName2 : "+"000001.SZ.mat");
        //List<File> stockList = finder.findOneStockInAllYearsDir("D:\\原始数据\\stock",stockName);

        //LOGGER.info("SIZE  : "+stockList.size());
        //aimstockProcessor(stockPath,"000001.SZ.mat","D:\\原始数据\\OutPutTest");
        //000001.SZ
        //000001.SZ.mat
        if(!"000001.SZ.mat".equalsIgnoreCase(stockName)){
            LOGGER.error("File name not equale ");
        }
        //aimstockProcessor(stockPath,stockName,"D:\\原始数据\\OutPutTest");

        testoneAimstockProcessor(aiStock,outputPath);

        LOGGER.info("DataProcessor 运行结束");
    }



    public static void aimstockProcessor(String stockPath,String aimStock,String outputPath) throws IOException, ParseException {
        //输出文件
        //000001.SZ.mat
        String [] sp = aimStock.split("\\.");
        String outfileName = sp[0]+sp[1]+".csv";
        String outfilePath =  outputPath + File.separator + outfileName;
        File outfile = new File(outfilePath);
        if(!outfile.exists()){
            outfile.createNewFile(); //不存在则创建 存在则追加
        }

        System.out.println("outfile :"+outfilePath);
        //开始合并一个文件
        DirFinder finder = new DirFinder();
        List<File> stockList = finder.findOneStockInAllYearsDir(stockPath,aimStock);

        for(int i=0;i<stockList.size();i++){
            File tmpStock = stockList.get(i);
            LOGGER.info("\ntmpStock "+tmpStock);
            MatColGetter.matReader(tmpStock,outfile);
        }
    }




    public static void testoneAimstockProcessor(String aimStock,String outputPath) throws IOException, ParseException {



        File stock = new File(aimStock);
        //输出文件

        String [] sp = aimStock.split("\\.");
        String outfileName ="201004_000002.SZ.csv";
        String outfilePath =  outputPath + File.separator + outfileName;
        File outfile = new File(outfilePath);

        if(!outfile.exists()){
            outfile.createNewFile(); //不存在则创建 存在则追加
        }

        System.out.println("outfile :"+outfilePath);

        //开始合并一个文件
        MatColGetter.matReader(stock,outfile);

    }


     /*       //要从list表中获取
        String aimStock1 = "000001.SZ.mat";
        String aimStock2 = "000002.SZ.mat";
        String aimStock3 = "000003.SZ.mat";
        String aimStock4 = "000004.SZ.mat";
        ArrayList<String> stockNameList = new ArrayList();
        stockNameList.add(aimStock1);
        stockNameList.add(aimStock2);
        stockNameList.add(aimStock3);
        stockNameList.add(aimStock4);*/
}
