package cn.com.spring.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理器适配器
 */
public class MyHandlerAdapter {

    public boolean supports(Object handler){ return (handler instanceof MyHandlerMapping);}


    MyModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        return null;
    }
}
