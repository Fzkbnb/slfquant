package com.slf.quant.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.slf.quant.dao.DamageListGoodsDao;
import com.slf.quant.dao.GoodsDao;
import com.slf.quant.dao.UserDao;
import com.slf.quant.domain.ServiceVO;
import com.slf.quant.domain.SuccessCode;
import com.slf.quant.entity.*;
import com.slf.quant.service.DamageListGoodsService;
import com.slf.quant.service.LogService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author slf
 * @date 2019/8/11 6:35
 * @description
 */
@Service
public class DamageListGoodsServiceImpl implements DamageListGoodsService {

    @Autowired
    private LogService logService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private DamageListGoodsDao damageListGoodsDao;
    @Autowired
    private GoodsDao goodsDao;

    @Override
    public ServiceVO save(DamageList damageList, String damageListGoodsStr) {

        // 使用谷歌Gson将JSON字符串数组转换成具体的集合
        Gson gson = new Gson();

        List<DamageListGoods> damageListGoodsList = gson.fromJson(damageListGoodsStr,new TypeToken<List<DamageListGoods>>(){}.getType());

        // 设置当前操作用户
        User currentUser = userDao.findUserByName((String) SecurityUtils.getSubject().getPrincipal());

        damageList.setUserId(currentUser.getUserId());

        // 保存商品报损单信息
        damageListGoodsDao.saveDamageList(damageList);

        // 保存商品报损单商品信息
        for(DamageListGoods damageListGoods : damageListGoodsList){

            damageListGoods.setDamageListId(damageList.getDamageListId());

            damageListGoodsDao.saveDamageListGoods(damageListGoods);

            // 修改商品库存，状态
            Goods goods = goodsDao.findByGoodsId(damageListGoods.getGoodsId());

            goods.setInventoryQuantity(goods.getInventoryQuantity()-damageListGoods.getGoodsNum());

            goods.setState(2);

            goodsDao.updateGoods(goods);

        }

        // 保存日志
        logService.save(new Log(Log.INSERT_ACTION, "新增商品报损单："+damageList.getDamageNumber()));

        return new ServiceVO<>(SuccessCode.SUCCESS_CODE, SuccessCode.SUCCESS_MESS);
    }

    @Override
    public Map<String, Object> list(String sTime, String eTime) {
        Map<String,Object> result = new HashMap<>();

        try {

            List<DamageList> damageListList = damageListGoodsDao.getDamagelist(sTime, eTime);

            logService.save(new Log(Log.SELECT_ACTION, "商品报损单据查询"));

            result.put("rows", damageListList);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return result;
    }

    @Override
    public Map<String, Object> goodsList(Integer damageListId) {
        Map<String,Object> map = new HashMap<>();

        try {

            List<DamageListGoods> damageListGoodsList = damageListGoodsDao.getDamageListGoodsByDamageListId(damageListId);

            logService.save(new Log(Log.SELECT_ACTION, "商品报损单商品信息查询"));

            map.put("rows", damageListGoodsList);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return map;
    }
}
