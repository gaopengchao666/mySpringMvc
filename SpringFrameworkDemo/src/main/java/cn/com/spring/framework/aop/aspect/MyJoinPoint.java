package cn.com.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author gaopengchao
 * 2019年4月18日
 */
public interface MyJoinPoint
{
    Method getMethod();
    
    Object[] getArguments();
    
    Object getThis();
    
    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
