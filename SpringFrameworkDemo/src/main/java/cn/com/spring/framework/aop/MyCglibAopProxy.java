package cn.com.spring.framework.aop;

import cn.com.spring.framework.aop.support.MyAdvisedSupport;

/**
 * @author gaopengchao
 * 2019年4月16日
 */
public class MyCglibAopProxy implements MyAopProxy
{
    public MyCglibAopProxy(MyAdvisedSupport config) {
    }
    
    @Override
    public Object getProxy()
    {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader)
    {
        return null;
    }

}
