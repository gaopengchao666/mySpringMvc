package cn.com.spring.demo.service.impl;

import cn.com.spring.demo.entity.User;
import cn.com.spring.demo.service.UserService;
import cn.com.spring.framework.annotation.MyService;

/**
 * @author gaopengchao
 * 2019年3月25日
 */
@MyService("userService")
public class UserServiceImpl implements UserService
{
    @Override
    public User queryUser(String userName)
    {
        User user = new User();
        user.setUserName(userName);
        user.setAge(18);
        user.setPassword("123.com");
        return user;
    }

    @Override
    public void addUser(String userName)
    {
        throw new NullPointerException("故意报错");
    }
}
