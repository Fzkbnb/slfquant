package com.slf.quant.strategy.grid.client;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;

/**
 * 计算器类
 */
@Slf4j
public class Calculater
{
    /**
     * 日盈利计算（单位usdt）profit = i*d*(f-2g);
     * @param i 单笔委托价值
     * @param d 单日预估对冲次数
     * @param f 止盈价差率
     * @param g 手续费率
     * @param e 行情价
     */
    public static void calProfitDay(BigDecimal i, BigDecimal d, BigDecimal f, BigDecimal g, BigDecimal e)
    {
        BigDecimal profit = i.multiply(d).multiply(f.subtract(BigDecimal.valueOf(2).multiply(g))).setScale(1, BigDecimal.ROUND_HALF_UP);
        log.info("根据设定参数，单日预估可实现盈利{}usdt。", profit);
    }
    
    public static void main(String[] args)
    {
        BigDecimal a = new BigDecimal("3000");// 投入金额
        BigDecimal b = new BigDecimal("0.4");// 单笔委托数量
        BigDecimal c = new BigDecimal("0.001");// 网格价差率
        BigDecimal d = new BigDecimal("100");// 单日预估对冲次数
        BigDecimal e = new BigDecimal("230");// 行情价
        BigDecimal f = new BigDecimal("0.0015");// 止盈价差率
        BigDecimal g = new BigDecimal("0.0002");// 手续费率
        BigDecimal h = new BigDecimal("3");// 杠杆率
        BigDecimal i = b.multiply(e).setScale(2, BigDecimal.ROUND_HALF_UP);// 单笔委托价值
        // 单日盈利计算
        Calculater.calProfitDay(i, d, f, g, e);
        // 波动阈值计算
        //建议1，进行委托上下限价设置时，建议取行情价*（1+/-最大可执行波动率/4）作为开多/开空的最大买入/最小卖出价格，然后根据预测的大趋势以及实际执行情况适当地同增同减这两个参数，
        //例如，预测将上涨，则尽可能同增限价并重启策略，再如，运行过程中波动均值长期处于起始行情价下方，则可同减限价并重启，注意，一定要同增同减，确保最大最小限价价差率不超过最大可执行波动率/2；
        //建议2：根据一段时间内的测试情况优化建议1中的相关阈值，并考虑是否可增大/减少投入金额
        Calculater.calMaxChange(a, b, c, e, h);


    }

    /**
     * 最大可执行波动率计算
     * @param a
     * @param b
     * @param c
     * @param e
     * @param h
     */
    public static void calMaxChange(BigDecimal a, BigDecimal b, BigDecimal c, BigDecimal e, BigDecimal h)
    {
        BigDecimal changePrice = a.multiply(h).divide(b, 8, BigDecimal.ROUND_HALF_UP).multiply(c).setScale(8, BigDecimal.ROUND_HALF_UP);
        BigDecimal changeRate = changePrice.multiply(BigDecimal.valueOf(100)).divide(e, 4, BigDecimal.ROUND_HALF_UP);
        log.info("根据设定参数，最大可执行波动率为{}%。", changeRate);
    }


}
