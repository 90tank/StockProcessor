package processor.controller;

import csv.tool.ReadByApachCsv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import path.constant.Constant;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CsvDataProcessorController {

    private static Logger LOGGER = LogManager.getLogger(CsvDataProcessorController.class.getName());

    public static void main(String[] args) throws IOException, ParseException {
        LOGGER.info("CsvDataProcessor  开始运行");

        String [] csvPath = Constant.CsvRootDirs;
        String outputPath = Constant.CsvOutPutPath;

        List<File> rootDirList = new ArrayList<>();
        for(int i=0;i<csvPath.length;i++){
            rootDirList.add(new File(csvPath[i]));
        }
        String cspPath = Constant.ListCsvPath;
        List<String> stockNameList  = ReadByApachCsv.getStockList(cspPath);

        for(int i=0;i<stockNameList.size();i++){
            String aimStock = stockNameList.get(i);
            String tmpOutFileName = aimStock+".csv";
            LOGGER.info("start process aimStock: "+aimStock);

            File tmpOutFile = new File(outputPath+"\\"+tmpOutFileName);

            if(!tmpOutFile.exists()){
                tmpOutFile.createNewFile();
            }
            List<File> aimCsvList = searchcsv(rootDirList,aimStock);
            LOGGER.info("aimCsvList size \n "+aimCsvList.size());
           /* for (File f :aimCsvList) {
                LOGGER.info("---->\n "+f.getAbsolutePath());
            }*/
            ReadByApachCsv.readAllAimCsv(aimCsvList,tmpOutFile);

            LOGGER.info("complete process aimStock: "+aimStock);
        }

        LOGGER.info("CsvDataProcessor 运行结束");
    }


    public static List<File> searchcsv(List<File> rootDirs, String aimStock ){
        String aimStockType = null;
        String aimCode = aimStock.substring(0,aimStock.length()-3);
        List<File> aimCsvList = new ArrayList<>();
        //判断 SZ SH
        if(aimStock.contains("sh")|| aimStock.contains("SH")|| aimStock.contains("Sh")||aimStock.contains("sH")){
            aimStockType = "SH";
        }else if(aimStock.contains("sz")|| aimStock.contains("SZ")|| aimStock.contains("Sz")||aimStock.contains("sZ")){
            aimStockType = "SZ";
        }

        //查找该年月份文件夹

        for(int i=0; i<rootDirs.size();i++){// 2016 / 2017
            File dir = rootDirs.get(i);
            File[] ymDirs = dir.listFiles();

            for(int k=0;k<ymDirs.length;k++){
                File tmpYmDir = ymDirs[k]; //具体的年月文件夹
                if(!tmpYmDir.isDirectory()){
                    continue; //跳过非文件夹
                }
                String dirName = tmpYmDir.getName();
                String dirType = "UNKONWN";
                if(dirName.contains("sh")|| dirName.contains("SH")|| dirName.contains("Sh")||dirName.contains("sH")){
                    dirType = "SH";
                }else if(dirName.contains("sz")|| dirName.contains("SZ")|| dirName.contains("Sz")||dirName.contains("sZ")){
                    dirType = "SZ";
                }
                if(!aimStockType.equalsIgnoreCase(dirType)){
                    continue; //跳过 文件夹与目标文件不符的
                }
                //进入具体年月对应 交易所的文件夹 并拿出所有文件



                File [] dayCsvAry =  tmpYmDir.listFiles();

                for(int n=0;n<dayCsvAry.length;n++){
                    File YMDdir = dayCsvAry[n];
                    File [] csvAry =  YMDdir.listFiles();

                    for(int m=0;m<csvAry.length;m++){
                        File tmpCsv = csvAry[m];
                        String tmpCsvName = tmpCsv.getName();
                        int endIndex = tmpCsvName.indexOf("_");
                        if(endIndex<0){
                            LOGGER.error(" tmpCsvName : "+tmpCsvName);
                        }
                        tmpCsvName = tmpCsvName.substring(0,endIndex);

                        if(tmpCsvName.equals(aimCode)){
                            aimCsvList.add(csvAry[m]); //搜索到的目标文件 添加到集合
                        }
                    }


                }



            }
        }
        return aimCsvList;
    }
}
