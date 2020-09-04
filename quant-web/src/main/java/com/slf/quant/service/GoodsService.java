package com.slf.quant.service;

import com.slf.quant.domain.ServiceVO;
import com.slf.quant.entity.Goods;

import java.util.Map;

/**
 * @author slf
 * @date 2019/7/27 17:22
 * @description
 */
public interface GoodsService {

    Map<String,Object> list(Integer page, Integer rows, String goodsName, Integer goodsTypeId);

    Map<String,Object> listInventory(Integer page, Integer rows, String codeOrName, Integer goodsTypeId);

    ServiceVO getCode();

    ServiceVO save(Goods goods);

    ServiceVO delete(Integer goodsId);

    Map<String,Object> getNoInventoryQuantity(Integer page,Integer rows,String nameOrCode);

    ServiceVO deleteStock(Integer goodsId);

    Map<String,Object> getHasInventoryQuantity(Integer page,Integer rows,String nameOrCode);

    ServiceVO saveStock(Integer goodsId,Integer inventoryQuantity,double purchasingPrice);

    Map<String,Object> listAlarm();
}
