package com.slf.quant.facade.consts;

public class KeyConst
{
    public static final String REDISKEY_DIVISOR           = "quant:divisor:";

    public static final String REDISKEY_STRATEGY_STATS       = "quant:strategy:stats:";

    public static String       quote_currencys            = "BTC,ETH,EOS,LTC,BCH,XRP,BSV,ETC,TRX";
    
    public static final String EXCHANGE_OKEX              = "okex";
    
    public static final String EXCHANGE_HUOBI             = "huobi";
    
    public static final String STRATEGYTYPE_PLADDPOSITION = "PLAddPos";
    
    public static final String STRATEGYTYPE_HEDGE         = "Hedge";
    
    public static final String STRATEGYTYPE_AVG           = "avg";
    
    public static final String STRATEGYTYPE_HEDGEUSDT     = "HedgeUsdt";
    
    public static final String STRATEGYTYPE_HEDGEINVERSE  = "HedgeInverse";
    
    public static final String STRATEGYTYPE_GRID          = "grid";
}
