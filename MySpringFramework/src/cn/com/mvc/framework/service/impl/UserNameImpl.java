package cn.com.mvc.framework.service.impl;

import cn.com.mvc.framework.annotation.MyService;
import cn.com.mvc.framework.service.UserService;

/**
 * @author gaopengchao
 * 2019年3月25日
 */
@MyService("userService")
public class UserNameImpl implements UserService
{
    @Override
    public String queryUser(String userName)
    {
        return "Hello,My name is " + userName;
    }

}
