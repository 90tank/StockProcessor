package select;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirFinder {


    public File findOneStockInOneYearDir(File yearDir,String stockName){
        //  String stockDirPath = "/home/young/Run/stock";
        File tmpDir  = yearDir;
        File[] filesOfYear =  tmpDir.listFiles();
        for (int i=0;i<filesOfYear.length;i++){

            File tmpStockFile = filesOfYear[i];
            if(tmpStockFile.isDirectory()){
                continue;
            }
            String tmpName = tmpStockFile.getName();
            if(tmpName.equalsIgnoreCase(stockName)){
                return tmpStockFile; //找到则结束 每个年月的文件夹中只有一个
            }
        }

        return null;
    }

    /**
     * @param stockPath
     * @param stockName
     * @return the aim stock list
     */
    public List<File> findOneStockInAllYearsDir(String stockPath, String stockName){
        File dir = new File(stockPath);
        File[] dirOfYears = dir.listFiles();
        if(dirOfYears==null){
//            System.out.println("years dir is null !!");
            return null;
        }

        List<File> aimStockList = new ArrayList();
        for (int i=0;i<dirOfYears.length;i++){
            File tmpYearDir = dirOfYears[i];
            File aimStock = findOneStockInOneYearDir(tmpYearDir,stockName);
            if(aimStock==null){
//                System.out.println("aim stock is null !!");
                continue;
            }

            aimStockList.add(aimStock);
        }
        return aimStockList;
    }













}
