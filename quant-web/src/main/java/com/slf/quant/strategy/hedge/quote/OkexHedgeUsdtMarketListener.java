package com.slf.quant.strategy.hedge.quote;

import java.util.Arrays;
import java.util.List;

import com.slf.quant.facade.model.OkexMarketModel;
import com.slf.quant.facade.model.QuoteDepth;
import com.slf.quant.facade.utils.QuantUtil;
import com.slf.quant.strategy.consts.TradeConst;
import com.slf.quant.strategy.grid.quote.AbstractMarketListener;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;


import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;

@Slf4j
@Service
public class OkexHedgeUsdtMarketListener extends AbstractMarketListener
{
    private static String DEPTH_SPOT     = "\"spot/depth5:%s\"";
    
    private static String DEPTH_CONTRACT = "\"futures/depth5:%s\"";
    
    private static String DEPTH_SWAP     = "\"swap/depth5:%s\"";
    
    @Override
    protected void subscribe(WebSocket ws)
    {
        // 订阅深度
        List<String> currencys = Arrays.asList(TradeConst.hedge_currencys.split(","));
        long curtime = System.currentTimeMillis();
        String cw = QuantUtil.getWeekDate(curtime, 0);
        String nw = QuantUtil.getWeekDate( curtime, 7);
        String cq = QuantUtil.getQuanterDate( curtime);
        String nq = QuantUtil.getNextQuanterDate(curtime);
        currencys.forEach(currency -> {
            // String topic_spot = getDepthTopic(DEPTH_SPOT, currency + "-USDT");
            // ws.send(topic_spot);
//            String topic_cw_c = getDepthTopic(DEPTH_CONTRACT, currency + "-USD-" + cw);
//            ws.send(topic_cw_c);
//            String topic_nw_c = getDepthTopic(DEPTH_CONTRACT, currency + "-USD-" + nw);
//            ws.send(topic_nw_c);
//            String topic_cq_c = getDepthTopic(DEPTH_CONTRACT, currency + "-USD-" + cq);
//            ws.send(topic_cq_c);
//            String topic_nq_c = getDepthTopic(DEPTH_CONTRACT, currency + "-USD-" + nq);
//            ws.send(topic_nq_c);
//             String topic_swap_u = getDepthTopic(DEPTH_SWAP, currency + "-USDT-SWAP");
//             ws.send(topic_swap_u);
            String topic_cw_u = getDepthTopic(DEPTH_CONTRACT, currency + "-USDT-" + cw);
            ws.send(topic_cw_u);
            String topic_nw_u = getDepthTopic(DEPTH_CONTRACT, currency + "-USDT-" + nw);
            ws.send(topic_nw_u);
            String topic_cq_u = getDepthTopic(DEPTH_CONTRACT, currency + "-USDT-" + cq);
            ws.send(topic_cq_u);
            String topic_nq_u = getDepthTopic(DEPTH_CONTRACT, currency + "-USDT-" + nq);
            ws.send(topic_nq_u);
        });
        // 订阅ticker和合约标记价格信息
    }
    
    private String getDepthTopic(String type, String symbol)
    {
        return "{\"args\":" + formatArgs(String.format(type, symbol)) + ",\"op\":\"subscribe\"}";
    }
    
    private static String formatArgs(String ... args)
    {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (String str : args)
        {
            if (str.isEmpty())
            {
                continue;
            }
            builder.append(str);
            if (args.length > 1 && ++count < args.length)
            {
                builder.append("\",\"");
            }
        }
        return builder.toString();
    }
    
    /**
     * 行情消息处理逻辑
     * @param message
     */
    @Override
    protected void handleStringMessage(String message)
    {
        try
        {
            OkexMarketModel model = JSON.parseObject(message, OkexMarketModel.class);
            String channel = model.getTable();
            if (isDepthModel(channel))
            {
                // 解析出深度模型类
                JSONArray json = JSON.parseArray(model.getData());
                QuoteDepth quoteDepthModel = JSON.parseObject(json.getString(0), QuoteDepth.class);
                if (CollectionUtils.isEmpty(quoteDepthModel.getAsks()) || CollectionUtils.isEmpty(quoteDepthModel.getBids()))
                { return; }
                quoteDepthModel.buildAskBid();
                String symbol = quoteDepthModel.getInstrument_id();
                TradeConst.okex_depth_map.put(symbol, quoteDepthModel);
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
    
    private boolean isDepthModel(String channel)
    {
        return StringUtils.isNotEmpty(channel) && channel.contains("depth5");
    }
}
