package com.slf.quant.service;

import com.slf.quant.domain.ServiceVO;
import com.slf.quant.entity.Unit;

import java.util.Map;

/**
 * @author slf
 * @date 2019/7/26 17:28
 * @description
 */
public interface UnitService {

    ServiceVO save(Unit unit);

    ServiceVO delete(Integer unitId);

    Map<String,Object> list();
}
