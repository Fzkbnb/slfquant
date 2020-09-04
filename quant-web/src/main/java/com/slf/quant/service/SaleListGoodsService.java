package com.slf.quant.service;

import com.slf.quant.domain.ServiceVO;
import com.slf.quant.entity.SaleList;

import java.util.Map;

/**
 * @author slf
 * @date 2019/8/7 8:57
 * @description
 */
public interface SaleListGoodsService {

    Integer getSaleTotalByGoodsId(Integer goodsId);

    ServiceVO save(SaleList saleList, String saleListGoodsStr);

    Map<String,Object> list(String saleNumber, Integer customerId, Integer state, String sTime,
                            String eTime);

    Map<String,Object> goodsList(Integer saleListId);

    ServiceVO delete(Integer saleListId);

    ServiceVO updateState(Integer saleListId);

    String count(String sTime, String eTime ,Integer goodsTypeId, String codeOrName);

    String getSaleDataByDay(String sTime, String eTime);

    String getSaleDataByMonth(String sTime, String eTime);
}
