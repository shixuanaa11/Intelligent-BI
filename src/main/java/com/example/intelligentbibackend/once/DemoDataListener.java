package com.example.intelligentbibackend.once;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class DemoDataListener implements ReadListener<IndexOrNameData> {


    /**
     * 每读一条数据都会调用这个方法
     * @param data
     * @param context
     */
    @Override
    public void invoke(IndexOrNameData data, AnalysisContext context) {
        System.out.println(data);
    }
    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
//        saveData();
        log.info("所有数据解析完成！");
    }


}