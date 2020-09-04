package com.slf.quant.strategy.hedge.quote;

import java.util.Arrays;
import java.util.List;

import com.slf.quant.strategy.consts.TradeConst;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;


import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;

/**
 * 火币合约websocket行情监听器
 */
@Slf4j
@Service
public class HuobiFutureMarketListener extends HuobiSpotMarketListener
{
    @Override
    protected void subscribe(WebSocket webSocket)
    {
        List<String> currencys = Arrays.asList(TradeConst.hedge_currencys.split(","));
        JSONObject req = new JSONObject();
        currencys.forEach(currency -> {
            String topic_cw = String.format(DEPTH, currency + "_CW", "step0");
            req.put(op, topic_cw);
            req.put("id", topic_cw);
            webSocket.send(req.toString());
            String topic_nw = String.format(DEPTH, currency + "_NW", "step0");
            req.put(op, topic_nw);
            req.put("id", topic_nw);
            webSocket.send(req.toString());
            String topic_cq = String.format(DEPTH, currency + "_CQ", "step0");
            req.put(op, topic_cq);
            req.put("id", topic_cq);
            webSocket.send(req.toString());
        });
    }
}
