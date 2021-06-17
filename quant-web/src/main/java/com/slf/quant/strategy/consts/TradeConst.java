package com.slf.quant.strategy.consts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.slf.quant.facade.entity.strategy.QuantQuoteChange;
import com.slf.quant.facade.model.PremiumRankModel;
import com.slf.quant.facade.model.QuoteDepth;
import com.slf.quant.facade.model.StrategyStatusModel;
import com.slf.quant.strategy.avg.client.AbstractAvgClient;
import com.slf.quant.strategy.grid.client.AbstractGridUsdtClient;
import com.slf.quant.strategy.hedge.client.usdt.AbstractHedgeUsdtClient;

public class TradeConst
{
    public static Map<Long, AbstractGridUsdtClient>      grid_running_map           = new ConcurrentHashMap<>();
    
    public static Map<Long, AbstractHedgeUsdtClient>     hedge_running_map          = new ConcurrentHashMap<>();
    
    public static Map<Long, AbstractAvgClient>           avg_running_map            = new ConcurrentHashMap<>();
    
    public static Map<String, QuoteDepth>                huobi_depth_map            = new ConcurrentHashMap<>();
    
    public static Map<String, QuoteDepth>                okex_depth_map             = new ConcurrentHashMap<>();
    
    public static Map<String, QuoteDepth>                okex_triangular_depth_map  = new ConcurrentHashMap<>();
    
    public static String                                 KEY_BUY                    = "buy";
    
    public static String                                 KEY_SELL                   = "sell";
    
    public static long                                   quoteTimeout               = 5000L;
    
    public static AtomicLong                             cacheLoseCount             = new AtomicLong(0);
    
    public static String                                 currencys                  = "BTC,ETH,EOS,LTC,BCH,BSV,ETC,XRP,TRX,LINK,UNI,1INCH,DOGE,SUSHI,DOT";
    
    public static BigDecimal                             slipRatio                  = new BigDecimal("0.7");                                                                                                                                                                                                                                                                      // 溢价率折扣率
    
    public static String                                 hedge_currencys            = "BTC,ETH,EOS,LTC,BCH,BSV,ETC,XRP,TRX,UNI,DOT";
    
    public static String                                 triangular_currencys       = "XEM,NAS,ZEN,CTXC,GTO,TRIO,TRB,WTC,DASH,NANO,WAVES,XRP,TRX,MDT,ZRX,AE,IOST,DOT,OMG,TRUE,JST,NULS,BAND,CMT,RSR,IOTA,CVC,MITH,GAS,LTC,BTM,KCASH,BTT,CRV,ALGO,INT,ATOM,SC,ORS,HYC,ABT,ONT,XLM,LINK,YFI,QTUM,KSM,MANA,MOF,OKB,EOS,SWFTC,EGT,ETC,MKR,MCO,ZEC,NEO,REN,HC,ZIL,LRC,XMR,ADA,YOU,ELF";
    
    public static Map<String, List<StrategyStatusModel>> strategy_stats_map         = new ConcurrentHashMap<>();
    
    public static Map<String, QuantQuoteChange>          okex_change_map            = new ConcurrentHashMap<>();
    
    public static List<PremiumRankModel>                 premiumRateRankList        = new ArrayList<>();
    
    public static final String                           OKEXV5_INSTTYPE_FUTURES    = "FUTURES";
    
    public static final String                           OKEXV5_INSTTYPE_SWAP       = "SWAP";
    
    public static final String                           KEY_V5                     = "v5";
    
    public static final String                           KEY_LONG                   = "long";
    
    public static final String                           KEY_SHORT                  = "short";
    
    // 保证金模式
    // 全仓：cross ； 逐仓： isolated
    public static final String                           OKEXV5_MARGINMODE_CROSS    = "cross";
    
    public static final String                           OKEXV5_MARGINMODE_ISOLATED = "isolated";
}
