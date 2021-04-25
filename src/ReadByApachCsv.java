import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class ReadByApachCsv {


    public static void main(String[] args) throws IOException {
        Reader in = new FileReader("/home/young/ProjectYoung/MatFileReader/src/600749_20170831.csv");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
        for (CSVRecord record : records) {
            String columnOne = record.get(0);
            String columnTwo = record.get(1);
//            System.out.println(columnOne);
            System.out.println(columnTwo);
        }
    }
}
