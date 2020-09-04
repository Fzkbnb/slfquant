package com.slf.quant.config;

import com.slf.quant.entity.User;
import com.slf.quant.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>File：GenericController.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2015 2015-4-21 下午1:46:45</p>
 * <p>Company: BloCain</p>
 *
 * @author Playguy
 * @version 1.0
 */
public abstract class BaseController
{
    @Autowired(required = false)
    private UserService userService;
    
    protected Long getOnlineUserId()
    {
        // 获取当前登录的用户名
        String userName = (String) SecurityUtils.getSubject().getPrincipal();
        // 根据用户名查询用户信息
        User user = userService.findUserByName(userName);
        if (null == user)
        { throw new RuntimeException("用户不存在！"); }
        return (long) user.getUserId();
    }
}
