/*
 * @(#)ZttxConst.java 2015-4-14 下午2:02:23
 * Copyright 2015 Playguy, Inc. All rights reserved. com.blocain
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.slf.quant.facade.consts;

/**
 * <p>File：GlobalConst.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2015 2015-4-14 下午2:02:23</p>
 * <p>Company: BloCain</p>
 *
 * @author Playguy
 * @version 1.0
 */
public class GlobalConst
{
    private GlobalConst()
    {// 防止实例化
    }
    
    /**
     * 当前页面
     */
    public static final Integer  DEFAULT_CURRENT_PAGE           = 1;
    
    /**
     * 分页大小
     */
    public static final Integer  DEFAULT_PAGE_SIZE              = 10;
    
    /**
     * 分页起始大小
     */
    public static final Integer  DEFAULT_START_INDEX            = 0;
    
    /**
     * 批处理大小
     */
    public static final Integer  DEFAULT_BATCH_SIZE             = 100;
    
    /**
     * 分割符
     */
    public static final char     SEPARATOR                      = ':';
    
    /**
     * The default interval: 3000 ms = 3 seconds.
     */
    public static final long     DEFAULT_INTERVAL               = 3000;
    
    /**
     * 国际化语言
     */
    public static final String   COOKIE_LOCALE                  = "locale";
    
    /**
     * 启用
     */
    public static final String   SWITCH_ENABLE                  = "enable";
    
    /**
     * 停用
     */
    public static final String   SWITCH_DISABLE                 = "disable";
    
    /**
     * 默认排序
     */
    public static final String   DEFAULT_SORT_ASC               = "asc";
    
    /**
     * OAUTH2.0
     */
    public static final String   BEARER_TYPE                    = "Bearer";
    
    /**
     * 默认的 refresh_token 的有效时长: 30天
     */
    public final static int      REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 30;
    
    /**
     * 默认的 access_token 的有效时长: 12小时
     */
    public final static int      ACCESS_TOKEN_VALIDITY_SECONDS  = 60 * 60 * 12;
    
    public static final String   SESSION_BOSS_ID                = "bid";
    
    public static final String   SESSION_ROBOT_ID               = "rid";

    public static final String   SESSION_WEB_ID                 = "sid";
    
    public static final String   SESSION_TRADE_ID               = "sid";
    
    public static final String   SESSION_QUANT_ID               = "qid";
    
    public static final String   SESSION_OAUTH_ID               = "oid";
    
    public static final String   REQUEST_USERNAME               = "username";
    
    public static final String   REQUEST_PASSWORD               = "password";
    
    public static final String   REQUEST_POLICY_SMS             = "sms";
    
    public static final String   REQUEST_POLICY_GA              = "ga";
    
    public static final String   OAUTH_LOGIN_VIEW               = "oauth_login";
    
    public static final String   OAUTH_LOGIN2_VIEW              = "oauth_check";
    
    public static final String   OAUTH_APPROVAL_VIEW            = "oauth_approval";
    
    public static final String   REQUEST_USER_OAUTH_APPROVAL    = "user_oauth_approval";
    
    public static final String   RESOURCE_SERVER_NAME           = "oauth2-server";
    
    public static final String   INVALID_CLIENT_DESCRIPTION     = "客户端验证失败，如错误的client_id/client_secret。";
    
    public static final String   INVALID_CODE_DESCRIPTION       = "错误的授权码";
    
    // 公用模块(如：注册、取回密码)
    public static final String   COMMON                         = "/common";
    
    // 远程调用
    public static final String   CLIENT                         = "/client";
    
    // 回调地址
    public static final String   CALLBACK                       = "/callback";
    
    // 帐户
    public static final String   ACCOUNT                        = "/account";

    // 通知
    public static final String   NOTICE                        = "/notice";

    // 资金
    public static final String   FUND                           = "/fund";

    // 基金
    public static final String   FUNDS                           = "/funds";

    // 钱包
    public static final String   WALLET                         = "/wallet";
    
    // 指数交易
    public static final String   FUTURES                        = "/futures";
    
    // 发行代币trade
    public static final String   EXCHANGE                       = "/exchange";
    
    // 发行代币boss
    public static final String   ICO                            = "/ico";
    
    // 系统功能
    public static final String   SYSTEM                         = "/system";
    
    // 证券信息
    public static final String   STOCK                          = "/stock";
    
    // 量化
    public static final String   QUANT                          = "/quant";
    
    // 量化-策略
    public static final String   STRATEGY                       = "/strategy";
    
    // 现货交易
    public static final String   SOPT                           = "/spot";
    
    // 合约交易
    public static final String   CONTRACT                       = "/contract";
    
    // 杠杆现货交易
    public static final String   LEVERAGED                      = "/leveraged";
    
    // 交割结算
    public static final String   SETTLEMENT                     = "/settlement";
    
    // 钱包管理
    public static final String   BITPAY                         = "/bitpay";
    
    // 监控管理
    public static final String   MONITOR                        = "/monitor";
    
    // 区块管理
    public static final String   BLOCK                          = "/block";
    
    // 委托
    public static final String   ENTRUST                        = "/entrust";
    
    // 成交
    public static final String   REALDEAL                       = "/realdeal";
    
    // API
    public static final String   API                            = "/api";
    
    // 配资通道
    public static final String   CHANNEL                        = "/channel";
    
    // 活动管理
    public static final String   ACTIVITY                       = "/activity";
    
    public static final String   BUY                            = "buy";
    
    public static final String   SELL                           = "sell";
    
    /**
     * 请求方式
     */
    public static final String   POST                           = "post";
    
    /**
     * UUID length
     */
    public static final Integer  UUID_SIZE                      = 32;
    
    /**
     * 默认UNID
     */
    public static final Long     DEFAULT_UNID                   = 10000L;
    
    /**
     * 生产环境名称
     */
    public static final String   PROJECT_NAME                   = "BIEX";
    
    /**
     * 开发环境名称
     */
    public static final String   PROJECT_DEV_NAME               = "BIEX_DEV";
    
    /**
     * 测试环境名称
     */
    public static final String   PROJECT_TEST_NAME              = "BIEX_TEST";
    
    /**
     * 亚马逊邮件推送服务
     */
    public static final String   EMAIL_PROVIDER_AMAZON          = "amazon";
    
    /**
     * 默认语言
     */
    public static final String   DEFAULT_LANGUAGE               = "en_US";
    
    /**
     * 排除敏感的关键字
     */
    public static final String[] blackStrPathPattern            = new String[]{"*pass*", "*Pwd*", "*Key*"};
    
    /**
     * 操作频率限制
     * 默认30次
     */
    public static final Integer  LOCK_INTERVAL_COUNT            = 30;
    
    /**
     * 操作标识
     */
    public static final String   OP                             = "op";
    
    /**
     * 消息
     */
    public static final String   MESSAGE                        = "message";
    
    /**
     * 登陆操作
     */
    public static final String   OP_LOGIN                       = "login";
    
    /**
     * 找回密码操作
     */
    public static final String   OP_FINDPWD                     = "findpwd";
    
    /**
     * 帐户模块
     */
    public static final String   OP_ACCOUNT_BIND_PHONE          = "account:bindPhone";
    
    public static final String   OP_ACCOUNT_BIND_EMAIL          = "account:bindEmail";
    
    /**
     * 强增强减模块
     */
    public static final String   OP_FUND_ADJUST                 = "fundAdjust";
    
    /**
     * 撮合交易模块
     */
    public static final String   OP_ENTRUSTVCOOINMONEY          = "entrustVcoinMoney";
    
    /**
     * 资金流水模块
     */
    public static final String   OP_FUND_CURRENT                = "fundCurrent";
    
    /**
     * 帳戶資產
     */
    public static final String   OP_FUND_ASSET                  = "fundAsset";
    
    /**
     * 提币申请模块-用于短信或GA次数判定
     */
    public static final String   OP_RAISE_DO_RAISE              = "raise:doRaise";
    
    /**
     * 内部行情撮合成交价--用于计算行情涨跌幅
     */
    public static final String   OP_RTQUOTATIONINFO             = "RtQuotationInfo";
    
    public static final String   STARTLOCATION                  = "start:location";
    
    /**
     * 钱包划拨
     */
    public static final String   BITPAY_WITHDRAW                = "bitpayWithdraw";

    /**
     * 交割结算状态
     */
    public static final String   SETTLEMENT_STATUS              = "settlement:status:";

    /**
     * UMENG消息推送APIKEY等
     */
    public static final String   UMENG_APIKEY_ANDROID           = "5d7f313f570df33c80000443";

    public static final String   UMENG_APPMASTERSECRET_ANDROID  = "le42b5esbfegf62v977zi9ck5wij5abr";

    public static final String   UMENG_APIKEY_IOS               = "5d7f32c33fc195fc4e000edc";

    public static final String   UMENG_APPMASTERSECRET_IOS      = "fassywvv63zv7cszmuiyufwac2git6pj";
}
