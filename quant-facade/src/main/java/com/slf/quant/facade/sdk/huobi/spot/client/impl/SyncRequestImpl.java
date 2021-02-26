package com.slf.quant.facade.sdk.huobi.spot.client.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.slf.quant.facade.sdk.huobi.spot.client.SyncRequestClient;
import com.slf.quant.facade.sdk.huobi.spot.client.exception.HuobiApiException;
import com.slf.quant.facade.sdk.huobi.spot.client.model.*;
import com.slf.quant.facade.sdk.huobi.spot.client.model.enums.AccountType;
import com.slf.quant.facade.sdk.huobi.spot.client.model.enums.CandlestickInterval;
import com.slf.quant.facade.sdk.huobi.spot.client.model.enums.EtfSwapType;
import com.slf.quant.facade.sdk.huobi.spot.client.model.enums.QueryDirection;
import com.slf.quant.facade.sdk.huobi.spot.client.model.request.*;

public class SyncRequestImpl implements SyncRequestClient
{
    private final RestApiRequestImpl requestImpl;
    
    SyncRequestImpl(RestApiRequestImpl requestImpl)
    {
        this.requestImpl = requestImpl;
    }
    
    @Override
    public List<Candlestick> getLatestCandlestick(String symbol, CandlestickInterval interval, int size)
    {
        return RestApiInvoker.callSync(requestImpl.getCandlestick(symbol, interval, null, null, size));
    }
    
    @Override
    public List<Candlestick> getCandlestick(CandlestickRequest request)
    {
        return RestApiInvoker
                .callSync(requestImpl.getCandlestick(request.getSymbol(), request.getInterval(), request.getStartTime(), request.getEndTime(), request.getSize()));
    }
    
    @Override
    public long getExchangeTimestamp()
    {
        return RestApiInvoker.callSync(requestImpl.getExchangeTimestamp());
    }
    
    @Override
    public PriceDepth getPriceDepth(String symbol, int size)
    {
        return RestApiInvoker.callSync(requestImpl.getPriceDepth(symbol, size));
    }
    
    @Override
    public PriceDepth getPriceDepth(String symbol)
    {
        return RestApiInvoker.callSync(requestImpl.getPriceDepth(symbol, 20));
    }
    
    @Override
    public LastTradeAndBestQuote getLastTradeAndBestQuote(String symbol)
    {
        BestQuote bestQuote = RestApiInvoker.callSync(requestImpl.getBestQuote(symbol));
        Trade lastTrade = getLastTrade(symbol);
        LastTradeAndBestQuote lastTradeAndBestQuote = new LastTradeAndBestQuote();
        lastTradeAndBestQuote.setBidAmount(bestQuote.getBidAmount());
        lastTradeAndBestQuote.setBidPrice(bestQuote.getBidPrice());
        lastTradeAndBestQuote.setAskAmount(bestQuote.getAskAmount());
        lastTradeAndBestQuote.setAskPrice(bestQuote.getAskPrice());
        lastTradeAndBestQuote.setLastTradePrice(lastTrade.getPrice());
        lastTradeAndBestQuote.setLastTradeAmount(lastTrade.getAmount());
        return lastTradeAndBestQuote;
    }
    
    @Override
    public Trade getLastTrade(String symbol)
    {
        List<Trade> trades = RestApiInvoker.callSync(requestImpl.getHistoricalTrade(symbol, null, 1));
        if (trades != null && trades.size() != 0)
        {
            return trades.get(trades.size() - 1);
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public List<Trade> getHistoricalTrade(String symbol, int size)
    {
        return RestApiInvoker.callSync(requestImpl.getHistoricalTrade(symbol, null, size));
    }
    
    @Override
    public List<Trade> getTrade(String symbol)
    {
        return RestApiInvoker.callSync(requestImpl.getTrade(symbol));
    }
    
    @Override
    public TradeStatistics get24HTradeStatistics(String symbol)
    {
        return RestApiInvoker.callSync(requestImpl.get24HTradeStatistics(symbol));
    }
    
    @Override
    public ExchangeInfo getExchangeInfo()
    {
        List<Symbol> symbolList = RestApiInvoker.callSync(requestImpl.getSymbols());
        List<String> stringList = RestApiInvoker.callSync(requestImpl.getCurrencies());
        ExchangeInfo exchangeInfo = new ExchangeInfo();
        exchangeInfo.setSymbolList(symbolList);
        exchangeInfo.setCurrencies(stringList);
        return exchangeInfo;
    }
    
    public List<Symbol> getSymbols()
    {
        return RestApiInvoker.callSync(requestImpl.getSymbols());
    }
    
    public List<String> getCurrencies()
    {
        return RestApiInvoker.callSync(requestImpl.getCurrencies());
    }
    
    public List<Currency> getCurrencyInfo(String currency, Boolean authorizedUser)
    {
        return RestApiInvoker.callSync(requestImpl.getCurrencyInfo(currency, authorizedUser));
    }
    
    @Override
    public BestQuote getBestQuote(String symbol)
    {
        return RestApiInvoker.callSync(requestImpl.getBestQuote(symbol));
    }
    
    @Override
    public List<Withdraw> getWithdrawHistory(String currency, long fromId, int size)
    {
        return getWithdrawHistory(currency, fromId, size, null);
    }
    
    @Override
    public List<Withdraw> getWithdrawHistory(String currency, long fromId, int size, QueryDirection direction)
    {
        return RestApiInvoker.callSync(requestImpl.getWithdrawHistory(currency, fromId, size, direction));
    }
    
    @Override
    public List<Deposit> getDepositHistory(String currency, long fromId, int size)
    {
        return getDepositHistory(currency, fromId, size, null);
    }
    
    public List<Deposit> getDepositHistory(String currency, long fromId, int size, QueryDirection direction)
    {
        return RestApiInvoker.callSync(requestImpl.getDepositHistory(currency, fromId, size, direction));
    }
    
    @Override
    public Long transfer(TransferRequest transferRequest)
    {
        return RestApiInvoker.callSync(requestImpl.transfer(transferRequest));
    }
    
    @Override
    public Long transferFutures(TransferFuturesRequest request)
    {
        return RestApiInvoker.callSync(requestImpl.transferFutures(request));
    }
    
    @Override
    public long applyLoan(String symbol, String currency, BigDecimal amount)
    {
        return RestApiInvoker.callSync(requestImpl.applyLoan(symbol, currency, amount));
    }
    
    @Override
    public long repayLoan(long loadId, BigDecimal amount)
    {
        return RestApiInvoker.callSync(requestImpl.repayLoan(loadId, amount));
    }
    
    @Override
    public List<Loan> getLoanHistory(LoanOrderRequest loanOrderRequest)
    {
        return RestApiInvoker.callSync(requestImpl.getLoan(loanOrderRequest));
    }
    
    @Override
    public long transferCrossMargin(CrossMarginTransferRequest request)
    {
        return RestApiInvoker.callSync(requestImpl.transferCrossMargin(request));
    }
    
    public long applyCrossMarginLoan(CrossMarginApplyLoanRequest request)
    {
        return RestApiInvoker.callSync(requestImpl.applyCrossMarginLoan(request));
    }
    
    public void repayCrossMarginLoan(CrossMarginRepayLoanRequest request)
    {
        RestApiInvoker.callSync(requestImpl.repayCrossMarginLoan(request));
    }
    
    public List<CrossMarginLoanOrder> getCrossMarginLoanHistory(CrossMarginLoanOrderRequest request)
    {
        return RestApiInvoker.callSync(requestImpl.getCrossMarginLoanHistory(request));
    }
    
    public CrossMarginAccount getCrossMarginAccount()
    {
        CrossMarginAccount res = null;
        try
        {
            res = RestApiInvoker.callSync(requestImpl.getCrossMarginAccount());
        }
        catch (Exception e)
        {
        }
        return res;
    }
    
    @Override
    public List<Account> getAccountBalance()
    {
        List<Account> accounts = RestApiInvoker.callSync(requestImpl.getAccounts());
        for (Account account : accounts)
        {
            List<Balance> balances = RestApiInvoker.callSync(requestImpl.getBalance(account));
            account.setBalances(balances);
        }
        return accounts;
    }
    
    @Override
    public Account getAccountBalance(AccountType accountType)
    {
        List<Account> accounts = RestApiInvoker.callSync(requestImpl.getAccounts());
        for (Account account : accounts)
        {
            if (account.getType() == accountType)
            {
                List<Balance> balances = RestApiInvoker.callSync(requestImpl.getBalance(account));
                account.setBalances(balances);
                return account;
            }
        }
        return null;
    }
    
    public Account getAccountBalance(AccountType accountType, String symbol)
    {
        if (AccountType.MARGIN != accountType)
        { return getAccountBalance(accountType); }
        if (symbol == null || symbol.trim().length() <= 0)
        { throw new HuobiApiException(HuobiApiException.INPUT_ERROR, "[INPUT] Margin Account Need Symbol"); }
        List<Account> accounts = RestApiInvoker.callSync(requestImpl.getAccounts());
        for (Account account : accounts)
        {
            if (account.getType() == accountType && account.getSubtype().equals(symbol))
            {
                List<Balance> balances = RestApiInvoker.callSync(requestImpl.getBalance(account));
                account.setBalances(balances);
                return account;
            }
        }
        return null;
    }
    
    /**
     * 获取现货币币账户指定币种余额
     * @param currency
     * @return
     */
    public SpotAccount getSpotBalanceByCurrency(String currency)
    {
        Account account = getAccountBalance(AccountType.SPOT);
        List<Balance> balances = account.getBalance(currency);
        SpotAccount spotAccount = new SpotAccount();
        spotAccount.setEnable(BigDecimal.ZERO);
        spotAccount.setFrozen(BigDecimal.ZERO);
        if (CollectionUtils.isNotEmpty(balances))
        {
            for (Balance balance1 : balances)
            {
                if (balance1.getType().toString().equals("trade"))
                {
                    spotAccount.setEnable(balance1.getBalance());
                }
                else if (balance1.getType().toString().equals("frozen"))
                {
                    spotAccount.setFrozen(balance1.getBalance());
                }
            }
        }
        spotAccount.setBalance(spotAccount.getEnable().add(spotAccount.getFrozen()));
        return spotAccount;
    }
    
    @Override
    public List<AccountHistory> getAccountHistory(AccountHistoryRequest request)
    {
        return RestApiInvoker.callSync(requestImpl.getAccountHistory(request));
    }
    
    @Override
    public Map<String, TradeStatistics> getTickers()
    {
        return RestApiInvoker.callSync(requestImpl.getTickers());
    }
    
    @Override
    public long createOrder(NewOrderRequest newOrderRequest)
    {
        return RestApiInvoker.callSync(requestImpl.createOrder(newOrderRequest));
    }
    
    @Override
    public List<Order> getOpenOrders(OpenOrderRequest openOrderRequest)
    {
        return RestApiInvoker.callSync(requestImpl.getOpenOrders(openOrderRequest));
    }
    
    @Override
    public void cancelOrder(String symbol, long orderId)
    {
        RestApiInvoker.callSync(requestImpl.cancelOrder(symbol, orderId));
    }
    
    @Override
    public void cancelOrderByClientOrderId(String symbol, String clientOrderId)
    {
        RestApiInvoker.callSync(requestImpl.cancelOrderByClientOrderId(symbol, clientOrderId));
    }
    
    @Override
    public void cancelOrders(String symbol, List<Long> orderIds)
    {
        RestApiInvoker.callSync(requestImpl.cancelOrders(symbol, orderIds));
    }
    
    @Override
    public BatchCancelResult cancelOpenOrders(CancelOpenOrderRequest request)
    {
        return RestApiInvoker.callSync(requestImpl.cancelOpenOrders(request));
    }
    
    @Override
    public Order getOrder(String symbol, long orderId)
    {
        return RestApiInvoker.callSync(requestImpl.getOrder(symbol, orderId));
    }
    
    @Override
    public Order getOrderByClientOrderId(String symbol, String clientOrderId)
    {
        return RestApiInvoker.callSync(requestImpl.getOrderByClientOrderId(symbol, clientOrderId));
    }
    
    @Override
    public List<MatchResult> getMatchResults(String symbol, long orderId)
    {
        return RestApiInvoker.callSync(requestImpl.getMatchResults(symbol, orderId));
    }
    
    @Override
    public List<MatchResult> getMatchResults(MatchResultRequest matchResultRequest)
    {
        return RestApiInvoker.callSync(requestImpl.getMatchResults(matchResultRequest));
    }
    
    @Override
    public List<DepositAddress> getDepositAddress(String currency)
    {
        return RestApiInvoker.callSync(requestImpl.getDepositAddress(currency));
    }
    
    @Override
    public WithdrawQuota getWithdrawQuota(String currency)
    {
        return RestApiInvoker.callSync(requestImpl.getWithdrawQuota(currency));
    }
    
    @Override
    public long withdraw(WithdrawRequest withdrawRequest)
    {
        return RestApiInvoker.callSync(requestImpl.withdraw(withdrawRequest));
    }
    
    @Override
    public void cancelWithdraw(String currency, long withdrawId)
    {
        RestApiInvoker.callSync(requestImpl.cancelWithdraw(currency, withdrawId));
    }
    
    @Override
    public List<Order> getHistoricalOrders(HistoricalOrdersRequest req)
    {
        return RestApiInvoker.callSync(requestImpl.getHistoricalOrders(req));
    }
    
    @Override
    public List<Order> getOrders(OrdersRequest req)
    {
        return RestApiInvoker.callSync(requestImpl.getOrders(req));
    }
    
    @Override
    public List<Order> getOrderHistory(OrdersHistoryRequest req)
    {
        return RestApiInvoker.callSync(requestImpl.getOrderHistory(req));
    }
    
    @Override
    public List<FeeRate> getFeeRate(String symbol)
    {
        return RestApiInvoker.callSync(requestImpl.getFeeRate(symbol));
    }

    @Override
    public JSONObject getApiKeyInfo(Long uid, String apikey) {
        return RestApiInvoker.callSync(requestImpl.getApiKeyInfo(uid,apikey));
    }

    @Override
    public JSONObject getUid() {
        return  RestApiInvoker.callSync(requestImpl.getUid());
    }

    @Override
    public long transferBetweenParentAndSub(TransferMasterRequest request)
    {
        return RestApiInvoker.callSync(requestImpl.transferBetweenParentAndSub(request));
    }
    
    @Override
    public List<Balance> getCurrentUserAggregatedBalance()
    {
        return RestApiInvoker.callSync(requestImpl.getCurrentUserAggregatedBalance());
    }
    
    @Override
    public List<CompleteSubAccountInfo> getSpecifyAccountBalance(long subId)
    {
        return RestApiInvoker.callSync(requestImpl.getSpecifyAccountBalance(subId));
    }
    
    @Override
    public List<Candlestick> getEtfCandlestick(String symbol, CandlestickInterval interval, Integer limit)
    {
        return RestApiInvoker.callSync(requestImpl.getETFCandlestick(symbol, interval, limit));
    }
    
    @Override
    public void etfSwap(String etfSymbol, int amount, EtfSwapType swapType)
    {
        RestApiInvoker.callSync(requestImpl.etfSwap(etfSymbol, amount, swapType));
    }
    
    @Override
    public List<EtfSwapHistory> getEtfSwapHistory(String etfSymbol, int offset, int size)
    {
        return RestApiInvoker.callSync(requestImpl.getEtfSwapHistory(etfSymbol, offset, size));
    }
    
    @Override
    public EtfSwapConfig getEtfSwapConfig(String etfSymbol)
    {
        return RestApiInvoker.callSync(requestImpl.getEtfSwapConfig(etfSymbol));
    }
    
    @Override
    public List<MarginBalanceDetail> getMarginBalanceDetail(String symbol)
    {
        return RestApiInvoker.callSync(requestImpl.getMarginBalanceDetail(symbol));
    }

    @Override
    public List<MarginLoanInfo> getMarginLoanInfo(String symbols)
    {
        return RestApiInvoker.callSync(requestImpl.getLoanInfo(symbols));
    }
}
