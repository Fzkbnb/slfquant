package com.slf.quant.facade.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class QuantUtil
{
    /**
     * 获取bigdecimal的小数位数
     */
    public static int getPrecision(BigDecimal number)
    {
        String str = number.stripTrailingZeros().toPlainString();
        if (str.indexOf(".") == -1)
        {
            return 0;
        }
        else
        {
            return str.substring(str.indexOf(".") + 1).length();
        }
    }
    
    /**
     * 获取周合约代码
     * @param curtime
     * @param plus
     * @return
     */
    public static String getWeekDate(long curtime, int plus)
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat foramt = new SimpleDateFormat("yyMMdd");
        if (6 < c.get(Calendar.DAY_OF_WEEK))
        {
            c.add(Calendar.DATE, 3);
        }
        else if (6 == c.get(Calendar.DAY_OF_WEEK))
        {
            // 如果是utc时间，则取上午8点作为交割时间点
            // 如果是北京时间，则取下午4点作为交割时间点
            // TimeZone timeZone = c.getTimeZone();
            c.set(Calendar.MINUTE, 0);// 控制分
            c.set(Calendar.SECOND, 0);// 控制秒
            // if ("中国标准时间".equals(timeZone.getDisplayName()))
            // {
            // // 北京时间
            // c.set(Calendar.HOUR_OF_DAY, 16);
            // }
            // else
            // {
            // // 其他时区都默认置为utc时间
            // c.set(Calendar.HOUR_OF_DAY, 8);
            // }
            // 北京时间
            c.set(Calendar.HOUR_OF_DAY, 16);
            long settleTime = c.getTimeInMillis();
            if (curtime > settleTime)
            {
                // 如果超过交割时间，则取下周5为当周交割日期
                c.add(Calendar.DATE, 3);
            }
        }
        c.add(Calendar.DATE, plus);
        c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        String week = foramt.format(c.getTime());
        return week;
    }
    
    /**
     * 获取季度合约代码
     * 规则：每年3，6，9，12月份的最后一个星期五作为季度合约交割日，且该日期不能与当周次周合约重复，也就是说当季度合约日期与次周合约重合时（季度合约交割日期前推2个星期），就应该改变季度合约代码了。
     *
     * 则：获取3，6，9，12月的最后一个星期五和倒数第三个星期五，
     *
     * @param curtime
     * @return
     */
    public static String getQuanterDate(long curtime)
    {
        Calendar settleDate;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(curtime);
        if (c.compareTo(getSettleTimeUpateTime(0, 3, 2)) == -1)
        {
            // 当前时间小于3月份倒数第三个周5交割时间点，则取3月份倒数第一个周五作为交割日期
            settleDate = getSettleTimeUpateTime(0, 3, 0);
        }
        else if (c.compareTo(getSettleTimeUpateTime(0, 6, 2)) == -1)
        {
            settleDate = getSettleTimeUpateTime(0, 6, 0);
        }
        else if (c.compareTo(getSettleTimeUpateTime(0, 9, 2)) == -1)
        {
            settleDate = getSettleTimeUpateTime(0, 9, 0);
        }
        else if (c.compareTo(getSettleTimeUpateTime(0, 12, 2)) == -1)
        {
            settleDate = getSettleTimeUpateTime(0, 12, 0);
        }
        else
        {
            settleDate = getSettleTimeUpateTime(1, 3, 0);
        }
        SimpleDateFormat foramt = new SimpleDateFormat("yyMMdd");
        return foramt.format(settleDate.getTime());
    }
    
    public static String getNextQuanterDate(long curtime)
    {
        Calendar settleDate;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(curtime);
        if (c.compareTo(getSettleTimeUpateTime(0, 3, 2)) == -1)
        {
            // 当前时间小于3月份倒数第三个周5交割时间点，则取3月份倒数第一个周五作为交割日期
            settleDate = getSettleTimeUpateTime(0, 6, 0);
        }
        else if (c.compareTo(getSettleTimeUpateTime(0, 6, 2)) == -1)
        {
            settleDate = getSettleTimeUpateTime(0, 9, 0);
        }
        else if (c.compareTo(getSettleTimeUpateTime(0, 9, 2)) == -1)
        {
            settleDate = getSettleTimeUpateTime(0, 12, 0);
        }
        else if (c.compareTo(getSettleTimeUpateTime(0, 12, 2)) == -1)
        {
            settleDate = getSettleTimeUpateTime(1, 3, 0);
        }
        else
        {
            settleDate = getSettleTimeUpateTime(1, 6, 0);
        }
        SimpleDateFormat foramt = new SimpleDateFormat("yyMMdd");
        return foramt.format(settleDate.getTime());
    }
    
    /**
     * 获取指定月份，最后一个周5前推n个星期的上午utc时间8点（如果是北京时间则应为下午4点）
     * @param monthNum
     * @param lastNum
     * @return
     */
    public static Calendar getSettleTimeUpateTime(int yearNum, int monthNum, int lastNum)
    {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, yearNum);
        c.set(Calendar.MONTH, monthNum);// 实际月份是参数月份的下个月
        c.set(Calendar.DAY_OF_MONTH, 1);// 实际月份第一天
        c.add(Calendar.DAY_OF_MONTH, -1);// 参数月份最后一天
        // 获取当月最后一的星期树
        int weekNum = c.get(Calendar.DAY_OF_WEEK);
        // 计算最后一个星期五所在日期
        if (weekNum < 6)
        {
            c.add(Calendar.DATE, -weekNum - 1);
        }
        else
        {
            c.add(Calendar.DATE, 6 - weekNum);
        }
        // 前推lastNum个星期
        c.add(Calendar.DATE, -lastNum * 7);
        // TimeZone timeZone = c.getTimeZone();
        c.set(Calendar.MINUTE, 0);// 控制分
        c.set(Calendar.SECOND, 0);// 控制秒
        // if ("中国标准时间".equals(timeZone.getDisplayName()))
        // {
        // // 北京时间
        // c.set(Calendar.HOUR_OF_DAY, 16);
        // }
        // else
        // {
        // // 其他时区都默认置为utc时间
        // c.set(Calendar.HOUR_OF_DAY, 8);
        // }
        // 北京时间
        c.set(Calendar.HOUR_OF_DAY, 16);
        return c;
    }
    
    /**
     * 判断是否处于交割期间
     * @return
     */
    public static boolean isInSettlement()
    {
        Date date = new Date();
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        String week = dateFm.format(date);
        if ("星期五".equals(week) || "Friday".equalsIgnoreCase(week))
        {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MINUTE, 0);// 控制分
            cal.set(Calendar.SECOND, 0);// 控制秒
            // 北京时间
            cal.set(Calendar.HOUR_OF_DAY, 16);// 控制时
            long start = cal.getTimeInMillis() - 120000;
            long end = start + 60000 * 32;
            long curtime = System.currentTimeMillis();
            if (curtime >= start && curtime < end)
            { return true; }
        }
        return false;
    }
}
