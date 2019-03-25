package cn.com.mvc.framework.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.mvc.framework.annotation.MyAutowired;
import cn.com.mvc.framework.annotation.MyController;
import cn.com.mvc.framework.annotation.MyRequestMapping;
import cn.com.mvc.framework.annotation.MyRequestParam;
import cn.com.mvc.framework.service.UserService;

/**
 * @author gaopengchao
 * 2019年3月25日
 */
@MyController
@MyRequestMapping("/user")
public class UserController
{
    @MyAutowired
    public UserService userService;
    
    @MyRequestMapping("/queryUser")
    public void queryUser(HttpServletRequest req,HttpServletResponse res, @MyRequestParam(value="name") String userName)
    {
        String result = userService.queryUser(userName);
        try
        {
            res.getWriter().write(result);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        } 
    }
    
    @MyRequestMapping("/addUser")
    public void addUser(HttpServletRequest req,HttpServletResponse res,@MyRequestParam("name") String userName)
    {
        try
        {
            res.getWriter().write("Add User:" + userName + " is Successed.");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        } 
    }
}
