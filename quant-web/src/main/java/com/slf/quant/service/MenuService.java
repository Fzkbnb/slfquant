package com.slf.quant.service;

import javax.servlet.http.HttpSession;

/**
 * @author slf
 * @date 2019/7/18 15:53
 * @description
 */
public interface MenuService {

    String loadMenu(HttpSession session);

    String loadCheckMenu(Integer roleId);

}
