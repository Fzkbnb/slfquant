package com.slf.quant.strategy.grid.quote;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.slf.quant.facade.consts.KeyConst;
import com.slf.quant.facade.entity.strategy.QuantQuoteChange;
import com.slf.quant.facade.model.OkexMarketModel;
import com.slf.quant.facade.model.OkexV5MarketModel;
import com.slf.quant.facade.model.QuoteDepth;
import com.slf.quant.facade.service.strategy.QuantQuoteChangeService;
import com.slf.quant.facade.utils.DateUtils;
import com.slf.quant.strategy.consts.TradeConst;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;

@Slf4j
@Service
public class OkexUsdtMarketListenerV5 extends AbstractMarketListener
{
    private static String           DEPTH        = "{\"op\": \"subscribe\",\"args\": [channels]}";
    
    private static String           DEPTHCHANNEL = "{\"channel\": \"books5\",\"instId\": \"symbol\"}";
    
    private ExecutorService         quoteEs      = Executors.newSingleThreadExecutor();
    
    @Autowired
    private QuantQuoteChangeService quantQuoteChangeService;
    
    @Override
    protected void subscribe(WebSocket ws)
    {
        // 订阅深度
        List<String> currencys = Arrays.asList(TradeConst.currencys.split(","));
        long curtime = System.currentTimeMillis();
        // String cw = QuantUtil.getWeekDate(curtime, 0).substring(5);
        // String nw = QuantUtil.getWeekSymbol("", curtime, 7).substring(5);
        // String cq = QuantUtil.getQuanterSymbol("", curtime).substring(5);
        // String nq = QuantUtil.getNextQuanterSymbol("", curtime).substring(5);
        List<String> channels = new ArrayList<>();
        currencys.forEach(currency -> {
            // String topic_spot = DEPTHCHANNEL.replace("symbol", currency + "-USDT");
            // channels.add(topic_spot);
            // String topic_cw = DEPTHCHANNEL.replace("symbol", currency + "-USDT-" + cw);
            // channels.add(topic_cw);
            // String topic_nw = DEPTHCHANNEL.replace("symbol", currency + "-USDT-" + nw);
            // channels.add(topic_nw);
            // String topic_cq = DEPTHCHANNEL.replace("symbol", currency + "-USDT-" + cq);
            // channels.add(topic_cq);
            // String topic_nq = DEPTHCHANNEL.replace("symbol", currency + "-USDT-" + nq);
            // channels.add(topic_nq);
            String topic_swap = DEPTHCHANNEL.replace("symbol", currency + "-USDT-SWAP");
            channels.add(topic_swap);
        });
        String channelsStr = channels.stream().map(String::valueOf).collect(Collectors.joining(","));
        String msg = DEPTH.replace("channels", channelsStr);
        ws.send(msg);
    }
    
    /**
     * 行情消息处理逻辑
     * {"arg":{"channel":"books5","instId":"BTC-USDT"},
     * "data":[{"asks":[["59126.1","0.70822538","0","2"],["59126.7","0.01","0","1"],"bids":[["59126","2.77571105","0","12"],["59122.9","0.02794019","0","4"]],"instId":"BTC-USDT","ts":"1617241933023"}]}
     * @param message
     */
    /**
     * 行情消息处理逻辑
     * @param message
     */
    @Override
    protected void handleStringMessage(String message)
    {
        try
        {
            OkexV5MarketModel model = JSON.parseObject(message, OkexV5MarketModel.class);
            if (null != model.getData())
            {
                // 解析出深度模型类
                JSONArray json = JSON.parseArray(model.getData());
                QuoteDepth quoteDepthModel = JSON.parseObject(json.getString(0), QuoteDepth.class);
                if (CollectionUtils.isEmpty(quoteDepthModel.getAsks()) || CollectionUtils.isEmpty(quoteDepthModel.getBids()))
                { return; }
                quoteDepthModel.buildAskBid();
                String symbol = quoteDepthModel.getInstId();
                TradeConst.okex_depth_map.put(symbol, quoteDepthModel);
                handleChangeRateData(symbol, quoteDepthModel, new BigDecimal("0.0015"));
                handleChangeRateData(symbol, quoteDepthModel, new BigDecimal("0.005"));
                // handleChangeRateData(symbol, quoteDepthModel, new BigDecimal("0.01"));
            }
            else
            {
                // log.info("okex接收到ws订阅类消息：{}", message);
            }
        }
        catch (Exception e)
        {
            log.info("okex行情websocket客户端onmessage处理异常：{}", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
    
    private void handleChangeRateData(String symbol, QuoteDepth quoteDepthModel, BigDecimal changeRate)
    {
        long firstSec = DateUtils.getCurrentDateFirstSec();
        String currency = symbol.substring(0, symbol.indexOf("-"));
        String key = currency + "|" + changeRate.toPlainString();
        QuantQuoteChange change = TradeConst.okex_change_map.get(key);
        if (null == change)
        {
            QuantQuoteChange query = new QuantQuoteChange();
            query.setExchange(KeyConst.EXCHANGE_OKEX);
            query.setCurrency(currency);
            query.setStartTime(firstSec);
            query.setChangeRate(changeRate);
            List<QuantQuoteChange> list = quantQuoteChangeService.findList(query);
            if (CollectionUtils.isNotEmpty(list))
            {
                change = list.get(0);
            }
        }
        if (null == change)
        {
            // 数据库无记录则新建一条
            change = new QuantQuoteChange();
            change.setExchange(KeyConst.EXCHANGE_OKEX);
            change.setCurrency(currency);
            change.setSymbol(symbol);
            change.setChangeCount(0);
            change.setChangeRate(changeRate);
            change.setStartTime(firstSec);
            change.setUpdateTime(System.currentTimeMillis());
            change.setOpenPrice(quoteDepthModel.getAvg());
            change.setClosePrice(quoteDepthModel.getAvg());
            change.setBasePrice(quoteDepthModel.getAvg());
            synInsert(change);
            TradeConst.okex_change_map.put(key, change);
        }
        // 比较当天第一秒与缓存第一秒，如果大于缓存，则新建一条,否则根据是否波动来更新记录
        if (firstSec > change.getStartTime())
        {
            change.setStartTime(firstSec);
            change.setChangeCount(0);
            change.setUpdateTime(System.currentTimeMillis());
            change.setOpenPrice(quoteDepthModel.getAvg());
            change.setClosePrice(quoteDepthModel.getAvg());
            change.setBasePrice(quoteDepthModel.getAvg());
            synInsert(change);
            TradeConst.okex_change_map.put(key, change);
        }
        else
        {
            BigDecimal lastPrice = quoteDepthModel.getAvg().compareTo(change.getBasePrice()) == 1 ? quoteDepthModel.getBid() : quoteDepthModel.getAsk();
            if (lastPrice.subtract(change.getBasePrice()).divide(change.getBasePrice(), 6, BigDecimal.ROUND_HALF_UP).abs().compareTo(changeRate) == 1)
            {
                // 触发波动阈值则更新数据库记录
                change.setChangeCount(change.getChangeCount() + 1);
                change.setClosePrice(quoteDepthModel.getAvg());
                change.setBasePrice(quoteDepthModel.getAvg());
                change.setUpdateTime(System.currentTimeMillis());
                quantQuoteChangeService.updateByPrimaryKey(change);
                TradeConst.okex_change_map.put(key, change);
            }
        }
    }
    
    private void synInsert(QuantQuoteChange change)
    {
        quoteEs.execute(() -> quantQuoteChangeService.insert(change));
    }
    
    private boolean isDepthModel(String channel)
    {
        return StringUtils.isNotEmpty(channel) && channel.contains("books5");
    }
}
