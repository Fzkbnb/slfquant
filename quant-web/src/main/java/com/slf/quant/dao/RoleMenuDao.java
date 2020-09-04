package com.slf.quant.dao;

import com.slf.quant.entity.RoleMenu;

/**
 * @author slf
 * @date 2019/7/20 20:33
 * @description
 */
public interface RoleMenuDao {

    // 根据角色id删除该角色的所有菜单权限
    Integer deleteRoleMenuByRoleId(Integer roleId);

    // 为角色新增一条菜单权限
    Integer save(RoleMenu roleMenu);
}
