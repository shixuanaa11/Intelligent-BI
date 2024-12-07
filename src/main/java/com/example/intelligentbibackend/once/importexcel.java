package com.example.intelligentbibackend.once;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.config.ResourceUnit;
import org.springframework.util.ResourceUtils;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class importexcel {
    public static void main(String[] args) {
        // since: 3.0.0-beta1
        String fileName  ="D:\\code\\Intelligent-BI\\Intelligent-BI-backend\\src\\main\\resources\\test1.xlsx";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
//        readByListener(fileName);
        try {
            readSyncByListener(fileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

//    同步读取
    public static void readByListener(String fileName) {
        EasyExcel.read(fileName, IndexOrNameData.class,new DemoDataListener()).sheet().doRead();
    }

//    异步读取
public static void readSyncByListener(String fileName) throws FileNotFoundException {
   File file = ResourceUtils.getFile("classpath:test1.xlsx");
    List<Map<Integer,String>> list = EasyExcel.read(file).excelType(ExcelTypeEnum.XLSX).sheet().headRowNumber(0).doReadSync();
    System.out.println(list);
}

}
