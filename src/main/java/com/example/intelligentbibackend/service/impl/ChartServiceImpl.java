package com.example.intelligentbibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.intelligentbibackend.model.domain.Chart;
import com.example.intelligentbibackend.service.ChartService;
import com.example.intelligentbibackend.mapper.ChartMapper;
import org.springframework.stereotype.Service;

/**
* @author HYR
* @description 针对表【chart(图表表)】的数据库操作Service实现
* @createDate 2024-11-26 03:05:27
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

}




