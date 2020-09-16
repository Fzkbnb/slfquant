package com.slf.quant.strategy.init;

import com.slf.quant.facade.consts.KeyConst;
import com.slf.quant.facade.service.strategy.QuantGridStatsService;
import com.slf.quant.facade.utils.SpringContext;
import com.slf.quant.strategy.avg.task.QuantAvgConfigScanTask;
import com.slf.quant.strategy.triangular.client.AbstractTriangularClient;
import com.slf.quant.strategy.triangular.quote.QuoteTtriangularWebsocketTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.slf.quant.strategy.grid.quote.QuoteWebsocketTask;
import com.slf.quant.strategy.grid.task.QuantGridConfigScanTask;
import com.slf.quant.strategy.hedge.task.QuoteAnalysisTask;
import com.slf.quant.strategy.hedge.task.QuoteHedgeWebsocketTask;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@DependsOn("springContext")
public class Initer implements ApplicationRunner
{
    @Autowired
    private QuantGridConfigScanTask       quantGridConfigScanTask;
    
    @Autowired
    private QuoteWebsocketTask            quoteWebsocketTask;
    
    @Autowired
    private QuoteHedgeWebsocketTask       quoteHedgeWebsocketTask;
    
    @Autowired
    private QuoteAnalysisTask             quoteAnalysisTask;
    
    @Autowired
    private QuoteTtriangularWebsocketTask quoteTtriangularWebsocketTask;
    
    @Autowired
    private QuantAvgConfigScanTask        quantAvgConfigScanTask;
    
    @Override
    public void run(ApplicationArguments args)
    {
//        quoteHedgeWebsocketTask.start();
//        quoteAnalysisTask.start();
        // todo 暂时单体结构，同时启动动瓶，网格两个策略
        log.info(">>>开始启动网格策略配置扫描任务<<<");
        quoteWebsocketTask.start();
        quantGridConfigScanTask.start();
        log.info(">>>开始启动调平策略配置扫描任务<<<");
        quantAvgConfigScanTask.start();
        // todo 待改造，目前只支持行情监控
        log.info(">>>开始启动套利任务<<<");
        AbstractTriangularClient client = new AbstractTriangularClient(null, null, null, null);
        quoteTtriangularWebsocketTask.start();
        client.start();
        // 根据命令行参数确定要启动的策略类型
        // String type = KeyConst.STRATEGYTYPE_GRID;
        // String type = args.getOptionValues("type").get(0);
        // if (KeyConst.STRATEGYTYPE_GRID.equalsIgnoreCase(type))
        // {
        // // --type=grid
        // log.info(">>>开始启动网格策略配置扫描任务<<<");
        // quoteWebsocketTask.start();
        // quantGridConfigScanTask.start();
        // }
        // else if (KeyConst.STRATEGYTYPE_HEDGE.equalsIgnoreCase(type))
        // {
        // // --type=hedge
        // quoteHedgeWebsocketTask.start();
        // quoteAnalysisTask.start();
        // }
        // else if (KeyConst.STRATEGYTYPE_AVG.equalsIgnoreCase(type))
        // {
        // // --type=avg
        // log.info(">>>开始启动调平策略配置扫描任务<<<");
        // quantAvgConfigScanTask.start();
        // }
        // else
        // {
        // log.info(">>>不支持的策略类型:{}<<<", type);
        // }
        // 后期如果有新增策略类型，在下方追加轮询任务即可。
    }
}
