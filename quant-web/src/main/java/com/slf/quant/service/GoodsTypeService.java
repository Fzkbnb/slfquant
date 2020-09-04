package com.slf.quant.service;

import com.slf.quant.domain.ServiceVO;

/**
 * @author slf
 * @date 2019/7/26 9:33
 * @description
 */
public interface GoodsTypeService {

    ServiceVO delete(Integer goodsTypeId);

    ServiceVO save(String goodsTypeName,Integer pId);

    String loadGoodsType();
}
