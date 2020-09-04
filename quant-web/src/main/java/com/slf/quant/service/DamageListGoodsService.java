package com.slf.quant.service;

import com.slf.quant.domain.ServiceVO;
import com.slf.quant.entity.DamageList;

import java.util.Map;

/**
 * @author slf
 * @date 2019/8/11 6:34
 * @description
 */
public interface DamageListGoodsService {

    ServiceVO save(DamageList damageList, String damageListGoodsStr);

    Map<String,Object> list(String sTime, String eTime);

    Map<String,Object> goodsList(Integer damageListId);
}
