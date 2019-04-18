package cn.com.spring.demo.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.spring.demo.entity.User;
import cn.com.spring.demo.service.UserService;
import cn.com.spring.framework.annotation.MyAutowired;
import cn.com.spring.framework.annotation.MyController;
import cn.com.spring.framework.annotation.MyRequestMapping;
import cn.com.spring.framework.annotation.MyRequestParam;
import cn.com.spring.framework.webmvc.servlet.MyModelAndView;

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
    public MyModelAndView queryUser(HttpServletRequest req,HttpServletResponse res, @MyRequestParam(value="name") String userName)
    {
        User user = userService.queryUser(userName);
        Map<String,Object> model = new HashMap<String,Object>(); 
        model.put("userName", user.getUserName());
        model.put("age", user.getAge());
        model.put("password", user.getPassword());
        MyModelAndView modelAndView = new MyModelAndView("first", model);
        return modelAndView;
    }
    
    @MyRequestMapping("/addUser")
    public void addUser(HttpServletRequest req,HttpServletResponse res,@MyRequestParam("name") String userName)
    {
        userService.addUser(userName);
    }
    
    @MyRequestMapping("/deleteUser")
    public void deleteUser(HttpServletRequest req,HttpServletResponse res,@MyRequestParam("name") String userName) throws IOException
    {
        res.getOutputStream().write("删除方法".getBytes());
    }
}
