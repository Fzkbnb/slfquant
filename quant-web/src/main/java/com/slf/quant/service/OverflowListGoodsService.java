package com.slf.quant.service;

import com.slf.quant.domain.ServiceVO;
import com.slf.quant.entity.OverflowList;

import java.util.Map;

/**
 * @author slf
 * @date 2019/8/11 7:48
 * @description
 */
public interface OverflowListGoodsService {

    ServiceVO save(OverflowList overflowList, String overflowListGoodsStr);

    Map<String,Object> list(String sTime, String eTime);

    Map<String,Object> goodsList(Integer overflowListId);
}
