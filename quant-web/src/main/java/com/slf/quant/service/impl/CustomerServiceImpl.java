package com.slf.quant.service.impl;

import com.slf.quant.dao.CustomerDao;
import com.slf.quant.domain.ServiceVO;
import com.slf.quant.domain.SuccessCode;
import com.slf.quant.entity.Customer;
import com.slf.quant.entity.Log;
import com.slf.quant.service.CustomerService;
import com.slf.quant.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author slf
 * @date 2019/7/25 16:36
 * @description
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private LogService logService;
    @Autowired
    private CustomerDao customerDao;

    @Override
    public Map<String, Object> list(Integer page, Integer rows, String customerName) {
        Map<String,Object> map = new HashMap<>();

        page = page == 0 ? 1 : page;
        int offSet = (page - 1) * rows;

        List<Customer> customers = customerDao.getCustomerList(offSet, rows, customerName);

        logService.save(new Log(Log.SELECT_ACTION,"分页查询客户"));

        map.put("total", customerDao.getCustomerCount(customerName));

        map.put("rows", customers);

        return map;
    }

    @Override
    public ServiceVO save(Customer customer) {

        if(customer.getCustomerId() == null){

            customerDao.saveCustomer(customer);
            logService.save(new Log(Log.INSERT_ACTION,"添加客户:"+customer.getCustomerName()));

        }else{

            customerDao.updateCustomer(customer);
            logService.save(new Log(Log.UPDATE_ACTION,"修改客户:"+customer.getCustomerName()));

        }
        return new ServiceVO<>(SuccessCode.SUCCESS_CODE, SuccessCode.SUCCESS_MESS);
    }

    @Override
    public ServiceVO delete(String ids) {

        String[] idArray = ids.split(",");

        for(String id : idArray){

            logService.save(new Log(Log.DELETE_ACTION,
                    "删除客户:" + customerDao.getCustomerById(Integer.parseInt(id)).getCustomerName()));

            customerDao.deleteCustomer(Integer.parseInt(id));

        }

        return new ServiceVO<>(SuccessCode.SUCCESS_CODE, SuccessCode.SUCCESS_MESS);
    }

    @Override
    public List<Customer> getComboboxList(String q) {
        return customerDao.getCustomerListByNameLike(q);
    }
}
