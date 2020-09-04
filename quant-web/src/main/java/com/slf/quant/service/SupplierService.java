package com.slf.quant.service;

import com.slf.quant.domain.ServiceVO;
import com.slf.quant.entity.Supplier;

import java.util.List;
import java.util.Map;

/**
 * @author slf
 * @date 2019/7/25 8:48
 * @description
 */
public interface SupplierService {

    List<Supplier> getComboboxList(String q);

    Map<String,Object> list(Integer page, Integer rows, String supplierName);

    ServiceVO save(Supplier supplier);

    ServiceVO delete(String ids);
}
