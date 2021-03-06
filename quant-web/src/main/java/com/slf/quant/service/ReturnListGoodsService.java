package com.slf.quant.service;

import com.slf.quant.domain.ServiceVO;
import com.slf.quant.entity.ReturnList;

import java.util.Map;

/**
 * @author slf
 * @date 2019/8/4 9:48
 * @description
 */
public interface ReturnListGoodsService {

    ServiceVO save(ReturnList returnList, String returnListGoodsStr);

    Map<String,Object> list(String returnNumber,
                            Integer supplierId,
                            Integer state,
                            String sTime,
                            String eTime);

    Map<String,Object> goodsList(Integer returnListId);

    ServiceVO delete(Integer returnListId);

    ServiceVO updateState(Integer returnListId);

    String count(String sTime, String eTime ,Integer goodsTypeId, String codeOrName);
}
