package complete.appender;

import csv.tool.ReadByApachCsv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import path.constant.Constant;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.List;

public class MatCsvAppender {

    private static Logger LOGGER = LogManager.getLogger(MatCsvAppender.class.getName());

    public static void main(String[] args) throws IOException {

        String matCsvDir = Constant.MatOutputPath;
        String csvCsvDir = Constant.CsvOutPutPath;

        List<String> stockNameList  = ReadByApachCsv.getStockList(Constant.ListCsvPath);
        //Merge 之前先判断 文件是否存在 尤其是Mat文件 如果不存在 则直接将下个文件夹中的文件当作结果文件
        for (int i=0;i<stockNameList.size();i++){

            String aimStock  = stockNameList.get(i); //目标文件

            File matDir = new File(matCsvDir);
            File csvDir = new File(csvCsvDir);

            if(!matDir.isDirectory()&&!csvDir.isDirectory()){
                return ;
            }

            File [] matCsvAry = matDir.listFiles();
            File [] csvCsvAry = csvDir.listFiles();

            File tmpMatCsv = null;
            File tmpCsvCsv = null;

            //更新为不直接操作mat原始文件
           /* for(int k=0;k<matCsvAry.length;k++){
                if(matCsvAry[k].getName().contains(aimStock)){
                    tmpMatCsv = matCsvAry[k];
                    break;
                }
            }*/


            for(int k=0;k<csvCsvAry.length;k++){
                if(csvCsvAry[k].getName().contains(aimStock)){
                    tmpCsvCsv = csvCsvAry[k];
                    break;
                }
            }

            String []  Paths =null;
//            String resultAppendPath = Constant.AppendOutputPath+File.separator+ aimStock + ".csv";
//           将文件往mat上拼接 提高效率
            String resultAppendPath = Constant.MatOutputPath+File.separator+ aimStock + ".csv";

            LOGGER.info(" resultAppendPath : "+resultAppendPath);

//            更新后的代码都走这个分支
            if(tmpCsvCsv!=null){
                //将csvCsv 移动到目标文件夹
                String []  originPaths = {tmpCsvCsv.getAbsolutePath()};
                Paths = originPaths;
            }

            if(tmpCsvCsv==null){
                continue;
            }
            //合并 或 移动 文件
            mergeFiles(Paths,resultAppendPath);

        }


    }

    public static boolean mergeFiles(String[] fpaths, String resultPath) {

        File[] files = new File[fpaths.length];

        for (int i = 0; i < fpaths.length; i ++) {
            files[i] = new File(fpaths[i]);
            if ( !files[i].exists() || !files[i].isFile()) {
                LOGGER.info("file not exist or not a  file  \n"+resultPath);
                return false;
            }
        }
        File resultFile = new File(resultPath);

        LOGGER.info("append result process start  \n"+resultPath);
        try {

            //更新为追加模式 往Mat文件上追加
            FileChannel resultFileChannel = new FileOutputStream(resultFile, true).getChannel();
            for (int i = 0; i < fpaths.length; i ++) {
                FileChannel blk = new FileInputStream(files[i]).getChannel();
                resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
                blk.close();
            }
            resultFileChannel.close();

            //如果运行到此处，说明文件合并成功
            //则删除合并前的csv源文件 保证硬盘容量可用  此时删除的主要是csv原始文件
            for (int i = 0; i < fpaths.length; i ++) {
                if(files[i]!=null&&files[i].exists()){
                    files[i].delete();
                }
            }


        } catch (FileNotFoundException e) {
            LOGGER.error("FileNotFoundException ",e);
            return false;
        } catch (IOException e) {
            LOGGER.error("IOException ",e);
            return false;
        }
        LOGGER.info("append result process complete  \n"+resultPath);
        return true;
    }



}
