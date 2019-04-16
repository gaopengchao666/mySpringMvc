package cn.com.spring.framework.aop;

/**
 * @author gaopengchao
 * 2019年4月16日
 */
public interface MyAopProxy
{
    Object getProxy();
    Object getProxy(ClassLoader classLoader);
}
