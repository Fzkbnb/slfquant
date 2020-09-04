package com.slf.quant.dao;

import com.slf.quant.entity.ReturnList;
import com.slf.quant.entity.ReturnListGoods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author slf
 * @date 2019/8/4 10:20
 * @description
 */
public interface ReturnListGoodsDao {


    Integer saveReturnList(ReturnList returnList);

    Integer saveReturnListGoods(ReturnListGoods returnListGoods);

    List<ReturnList> getReturnlist(@Param("returnNumber") String returnNumber,
                                   @Param("supplierId") Integer supplierId,
                                   @Param("state") Integer state,
                                   @Param("sTime") String sTime,
                                   @Param("eTime") String eTime);

    List<ReturnListGoods> getReturnListGoodsByReturnListId(Integer returnListId);

    ReturnList getReturnList(Integer returnListId);

    Integer deleteReturnListById(Integer returnListId);

    Integer deleteReturnListGoodsByReturnListId(Integer returnListId);

    Integer updateState(Integer returnListId);

    List<ReturnListGoods> getReturnListGoods(@Param("returnListId") Integer returnListId,
                                             @Param("goodsTypeId") Integer goodsTypeId,
                                             @Param("codeOrName") String codeOrName);
}
