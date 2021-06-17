package com.slf.quant.strategy.hedge.quote;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.slf.quant.facade.utils.QuantUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.slf.quant.facade.consts.KeyConst;
import com.slf.quant.facade.entity.strategy.QuantQuoteChange;
import com.slf.quant.facade.model.OkexV5MarketModel;
import com.slf.quant.facade.model.QuoteDepth;
import com.slf.quant.facade.service.strategy.QuantQuoteChangeService;
import com.slf.quant.facade.utils.DateUtils;
import com.slf.quant.strategy.consts.TradeConst;
import com.slf.quant.strategy.grid.quote.AbstractMarketListener;

import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;

@Slf4j
@Service
public class OkexHedgeUsdtMarketListenerV5 extends AbstractMarketListener
{
    private static String           DEPTH        = "{\"op\": \"subscribe\",\"args\": [channels]}";
    
    private static String           DEPTHCHANNEL = "{\"channel\": \"books5\",\"instId\": \"symbol\"}";
    
    @Override
    protected void subscribe(WebSocket ws)
    {
        // 订阅深度
        List<String> currencys = Arrays.asList(TradeConst.currencys.split(","));
        long curtime = System.currentTimeMillis();
        String cw = QuantUtil.getWeekDate(curtime, 0);
        String nw = QuantUtil.getWeekDate(curtime, 7);
        String cq = QuantUtil.getQuanterDate(curtime);
        String nq = QuantUtil.getNextQuanterDate(curtime);
        List<String> channels = new ArrayList<>();
        currencys.forEach(currency -> {
            // String topic_spot = DEPTHCHANNEL.replace("symbol", currency + "-USDT");
            // channels.add(topic_spot);
            String topic_cw = DEPTHCHANNEL.replace("symbol", currency + "-USDT-" + cw);
            channels.add(topic_cw);
            String topic_nw = DEPTHCHANNEL.replace("symbol", currency + "-USDT-" + nw);
            channels.add(topic_nw);
            String topic_cq = DEPTHCHANNEL.replace("symbol", currency + "-USDT-" + cq);
            channels.add(topic_cq);
            String topic_nq = DEPTHCHANNEL.replace("symbol", currency + "-USDT-" + nq);
            channels.add(topic_nq);
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
                quoteDepthModel.setInstrument_id(quoteDepthModel.getInstId());
                if (CollectionUtils.isEmpty(quoteDepthModel.getAsks()) || CollectionUtils.isEmpty(quoteDepthModel.getBids()))
                { return; }
                quoteDepthModel.buildAskBid();
                String symbol = quoteDepthModel.getInstId();
                TradeConst.okex_depth_map.put(symbol, quoteDepthModel);
            }
            else
            {
                // log.info("okex接收到ws订阅类消息：{}", message);
            }
        }
        catch (Exception e)
        {
            log.info("okex v5行情websocket客户端onmessage处理异常：{}", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
