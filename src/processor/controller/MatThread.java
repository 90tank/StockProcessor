package processor.controller;

import csv.tool.ReadByApachCsv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import path.constant.Constant;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class MatThread extends Thread {
    private static Logger LOGGER = LogManager.getLogger(MatThread.class.getName());
    String stockPath = Constant.StockPath;
    String outputPath = Constant.MatOutputPath;

    private List<String> csvList;

    public MatThread( List<String>  tmpcsvList) {
        csvList = tmpcsvList;
    }

    @Override
    public void run() {

        for(int k = 0;k<csvList.size();k++){

            String csvlistFile = csvList.get(k);
            List<String> stockNameList = null;
            try {
                //由于4个线程 对文件的操作不存在 竞争，这里无需保护
                stockNameList = ReadByApachCsv.getUnCompleteStockList(csvlistFile); //使用新方法 获取没有处理的股票

                LOGGER.info(Thread.currentThread().getName()+"process this list : "+csvlistFile);
                for (int i = 0; i < stockNameList.size(); i++) {

                    String aimStock = stockNameList.get(i) + ".mat";
                    LOGGER.info(Thread.currentThread().getName()+"process this aimStock : "+aimStock);
                    MatDataProcessoerController.aimstockProcessor(stockPath, aimStock, outputPath);
                }
            } catch (IOException | ParseException e) {
                LOGGER.error("exception when process: "+csvlistFile+"exception info: "+e);
            }
        }



    }
}
