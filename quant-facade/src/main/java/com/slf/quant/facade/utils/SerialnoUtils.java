/*
 * @(#)SerialNumberUtils.java 2014-1-8 下午1:07:00
 * Copyright 2014 Playguy, Inc. All rights reserved. com.blocain
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.slf.quant.facade.utils;

import java.util.Random;
import java.util.UUID;

import com.slf.quant.facade.bean.IdWorker;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SerialnoUtils
{
    static String   TABLE    = "0123456789";
    
    static IdWorker idworker = new IdWorker(1, 1);//这列采用默认写死配置，暂不提供配置接口
    
    // 私有构造器，防止类的实例化
    private SerialnoUtils()
    {
        super();
    }
    
    /**
     * 创建数据库主键
     *
     * @return String 数据库主键
     */
    public synchronized static Long buildPrimaryKey()
    {
        if (null == idworker)
        {
            log.error("未指定workerId和centerId,系统将都用默认值(workerId:1,centerId:1)");
            idworker = new IdWorker(1, 1);
        }
        return idworker.nextId();
    }
    
    /**
     * 创建唯一编码
     *
     * @return String 数据库主键
     */
    public static String buildUUID()
    {
        String uuid = UUID.randomUUID().toString();
        return StringUtils.replace(uuid, "-", "").toLowerCase();
    }
    
    /**
     * 创建指定长度的随机数
     * @param length
     * @return
     */
    public static String randomNum(int length)
    {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i)
        {
            int number = random.nextInt(TABLE.length());
            sb.append(TABLE.charAt(number));
        }
        return sb.toString();
    }
}
