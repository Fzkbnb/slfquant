package com.slf.quant.strategy.consts;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.slf.quant.facade.model.QuoteDepth;
import com.slf.quant.facade.model.StrategyStatusModel;
import com.slf.quant.strategy.avg.client.AbstractAvgClient;
import com.slf.quant.strategy.grid.client.AbstractGridUsdtClient;

public class TradeConst
{
    public static Map<Long, AbstractGridUsdtClient>      grid_running_map          = new ConcurrentHashMap<>();
    
    public static Map<Long, AbstractAvgClient>           avg_running_map           = new ConcurrentHashMap<>();
    
    public static Map<String, QuoteDepth>                huobi_depth_map           = new ConcurrentHashMap<>();
    
    public static Map<String, QuoteDepth>                okex_depth_map            = new ConcurrentHashMap<>();
    
    public static Map<String, QuoteDepth>                okex_triangular_depth_map = new ConcurrentHashMap<>();
    
    public static String                                 KEY_BUY                   = "buy";
    
    public static String                                 KEY_SELL                  = "sell";
    
    public static long                                   quoteTimeout              = 5000L;
    
    public static AtomicLong                             cacheLoseCount            = new AtomicLong(0);
    
    public static String                                 currencys                 = "BTC,ETH,EOS,LTC,BCH,BSV,ETC,XRP,TRX";
    
    public static BigDecimal                             slipRatio                 = new BigDecimal("0.7");                                                                                                                                                                                                                                                                           // 溢价率折扣率
    
    public static String                                 hedge_currencys           = "BTC,ETH,EOS,LTC,BCH,BSV,ETC,XRP,TRX";
    
    public static String                                 triangular_currencys      = "XEM,NAS,ZEN,CTXC,GTO,TRIO,TRB,WTC,DASH,NANO,WAVES,XRP,TRX,MDT,ZRX,AE,IOST,DOT,OMG,TRUE,JST,NULS,BAND,CMT,RSR,IOTA,CVC,MITH,GAS,LTC,BTM,KCASH,BTT,CRV,LEND,ALGO,INT,ATOM,SC,ORS,HYC,ABT,ONT,XLM,LINK,YFI,QTUM,KSM,MANA,MOF,OKB,EOS,SWFTC,EGT,ETC,MKR,MCO,ZEC,NEO,REN,HC,ZIL,LRC,XMR,ADA,YOU,ELF";
    
    public static Map<String, List<StrategyStatusModel>> grid_stats_map            = new ConcurrentHashMap<>();
}
